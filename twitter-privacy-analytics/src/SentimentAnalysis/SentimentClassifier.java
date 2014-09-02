package SentimentAnalysis;

import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.LMClassifier;
import com.aliasi.util.AbstractExternalizable;
import java.io.File;
import java.io.IOException;


public class SentimentClassifier {

    String[] categories;
    LMClassifier clazz;

    public SentimentClassifier() {
        try {
            clazz = (LMClassifier) AbstractExternalizable.readObject(new File(Config.classifierPath));
            categories = clazz.categories();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public String classify(String text) {
        ConditionalClassification clazzification = clazz.classify(text);
        return clazzification.bestCategory();
    }
}
