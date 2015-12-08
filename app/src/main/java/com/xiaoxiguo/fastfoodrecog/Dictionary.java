package com.xiaoxiguo.fastfoodrecog;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shawn on 11/29/15.
 * Use classic singleton for the dictionary.
 *
 */
public class Dictionary {

    private String restName;
    private int size;
    private int detect;
    private int descript;

    private Mat centers;

    public List<ImageFeat> imagefeatList;

    public Mat getCenters() {
        return centers;
    }

    private static final TermCriteria termCriteria =
            new TermCriteria(TermCriteria.COUNT, 1000, 0.1);

    /**
     * Construct an empty dictionary with specific size and name
     * @param size The size of the dictionary.
     * @param detect The feature detection method used.
     * @param descript The feature description method used.
     */
    public Dictionary(int size, int detect, int descript) {
        this.size = size;
        this.detect = detect;
        this.descript = descript;
    }


    /**
     * Build a specific dictionary for a specific restaurant.
     * @param folder The folder holding all images of the restaurant.
     */
    public void build(final File folder) {
        this.imagefeatList = new ArrayList<ImageFeat>();
        this.restName = folder.getName();

        // get codewords of all images
        getCodewordsOneRest(folder);

        // cluster into k and calculate bag of
        clusterCodewords(size);
    }


    /**
     * Get all features of all images in one instance.
     * @param instFolder The instance folder.
     */
    private void getCodewordsOneInst(final File instFolder, int foodId) throws IOException{

        // Get background image first
        Image background = new Image();
        for (final File file : instFolder.listFiles()) {
            if (file.getName().equals("back.jpg")) {
                background.read(file.getPath());
            }
        }

        // extract surfs for each image and add to the list
        for (final File file : instFolder.listFiles()) {
            String filename = file.getPath();
            if (filename.endsWith(".jpg") && !filename.endsWith("back.jpg")) {
                FoodImage image = new FoodImage();
                image.read(filename);
                Mat mask = image.extractBackgroundMask(background);
                Mat surf = image.extractFeatures(mask, detect, descript);
                System.out.println("Extracted " + surf.size() + " features for " + filename);

                // Save filename, surfs, foodId and add it to list
                ImageFeat imagefeat = new ImageFeat();
                imagefeat.setImgName(filename).setFeatures(surf).setFoodId(foodId);
                imagefeatList.add(imagefeat);
            }
        }
    }


    /**
     * Get all features of all images in one restaurant.
     * @param restFolder The restaurant folder.
     */
    private List<Mat> getCodewordsOneRest(final File restFolder) {
        List<Mat> codewordsList = new ArrayList<Mat>();
        for (final File foodFolder : restFolder.listFiles()) {
            if (foodFolder.isDirectory()) {
                int foodId = Integer.parseInt(foodFolder.getName());
                for (final File instFolder : foodFolder.listFiles()) {
                    if (instFolder.isDirectory()) {
                        try {
                            getCodewordsOneInst(instFolder, foodId);
                        } catch (IOException e) {
                            System.out.println("Bad instance: " + instFolder.getPath());
                        }
                    }
                }
            }
        }
        return codewordsList;
    }


    /**
     * Cluster all codewords into k clusters.
     * @param k Number of clusters
     */
    protected void clusterCodewords(int k) {
        System.out.println("Clustering into " + k + "...");
        // merge all codewords into one mat
        Mat codewordsAll = new Mat();
        List<Mat> codewordsList = new ArrayList<Mat>(imagefeatList.size());
        for (ImageFeat imagefeat : imagefeatList) {
            codewordsList.add(imagefeat.getFeatures());
        }
        Core.vconcat(codewordsList, codewordsAll);
        Mat labelsAll = new Mat();
        Mat kmeansCenter = new Mat();
        Core.kmeans(codewordsAll, k, labelsAll, termCriteria, 3, Core.KMEANS_RANDOM_CENTERS, kmeansCenter);

        // save centers to dictionary
        this.centers = kmeansCenter;

        // seperate labels for each image and calculate bag of surfs
        System.out.println("Calculating bag of features");
        int start;
        int end = 0;
        for (ImageFeat imagefeat : imagefeatList) {
            start = end;
            end = end + imagefeat.getFeatures().rows();
            Mat labels = labelsAll.rowRange(start, end);
            BagOfFeature bagOfFeature = new BagOfFeature(size, labels);

            // save bag of surfs and add it to list
            imagefeat.setBagOfFeat(bagOfFeature);
        }
    }

    private String getDictName() {
        String dictName = restName + "_" + size;
        return dictName;
    }

    /**
     * Save dictionary info to file.
     * @param path Output folder.
     * @throws IOException If writing to file failed.
     */
    public void save(String path) throws IOException{
        String filename = path + getDictName() + "_Dict.txt";
        try {
            PrintWriter writer = new PrintWriter(filename);
            writer.println("Name:" + restName);
            writer.println("Detect:" + detect);
            writer.println("Describe:" + descript);
            writer.println("Size:" + size);
            writer.println("Length:" + centers.cols());
            writer.print(centers.dump());
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            throw new IOException("Failed to write dictionary to " + filename);
        }
        System.out.println("Saved dictionary to " + filename);
    }

    /**
     * Save debug data of building a dictionary.
     * @param path Output path
     * @throws IOException If writing failed.
     */
    public void saveDatas(String path) throws IOException {
        String filename = path + getDictName() + "_Datas.csv";
        try {
            PrintWriter writer = new PrintWriter(filename);
            writer.println("FoodImage,NumOfFeats,BagOfSurf,FoodId");
            for (ImageFeat imagefeat : imagefeatList) {
                writer.println(imagefeat.toString());
                writer.flush();
            }
            writer.close();
        } catch (FileNotFoundException e) {
            throw new IOException("Failed to write to " + filename);
        }
        System.out.println("Saved datas to " + filename);
    }

    /**
     * Load datas for trainning.
     * @param filename File name should be Path/Method/RestName_Size_Datas.csv;
     * @param trainDataList List of all bug of features.
     * @param trainLabelList List of labels corresponding to that bag of features.
     * @throws IOException If file is not found.
     */
    public static void loadDatas(String filename, List<Mat> trainDataList, List<Integer> trainLabelList) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        try {
            String line = null;
            String title = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String data[] = line.split(",");
                // get each train data
                BagOfFeature bagOfSurf = new BagOfFeature(Integer.parseInt(data[1]), data[2]);
                trainDataList.add(bagOfSurf.toMat());
                // get each train label
                trainLabelList.add(Integer.parseInt(data[3]));
            }
        } finally {
            reader.close();
        }
    }

    /**
     * Not implemented yet.
     * @param filename Path of file that saved dictionary info.
     * @return Dictionary instance.
     * @throws IOException If the filepath is not valid.
     */
    public static Dictionary load(Reader reader) throws IOException{

        BufferedReader bufferedReader = new BufferedReader(reader);
        // read header
        String name = bufferedReader.readLine().split(":")[1];
        int detect = Integer.parseInt(bufferedReader.readLine().split(":")[1]);
        int describe = Integer.parseInt(bufferedReader.readLine().split(":")[1]);
        int size = Integer.parseInt(bufferedReader.readLine().split(":")[1]);
        int length = Integer.parseInt(bufferedReader.readLine().split(":")[1]);

        // initialize dictionary.
        Dictionary dictionary = new Dictionary(size, detect, describe);
        dictionary.restName = name;
        dictionary.centers = new Mat(size, length, CvType.CV_32FC1);

        String line = null;
        int i = 0;
        while ((line = bufferedReader.readLine()) != null) {
            String[] row;
            if (line.startsWith("[")) {
                row = line.substring(1, line.length()-1).split(",");
            } else {
                row = line.substring(0, line.length()-1).split(",");
            }
            for (int j = 0; j < row.length; j++) {
                dictionary.centers.put(i, j, Double.parseDouble(row[j]));
            }
            i++;
        }
        return dictionary;
    }
}

