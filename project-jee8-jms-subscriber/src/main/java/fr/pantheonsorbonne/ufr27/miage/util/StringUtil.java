package fr.pantheonsorbonne.ufr27.miage.util;

import javax.jms.JMSException;
import javax.jms.Topic;

public class StringUtil {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public static String printColor(String out, String color) {
        return color + out + ANSI_RESET;
    }

    public static void clearConsoleAndPrintName(Topic topic, String type) {
        try {
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                    printColor("======= SNCF INFOGARE : ", ANSI_BLUE) +
                    printColor(topic.getTopicName().replaceAll("Topic", ""), ANSI_RED) +
                    printColor(" [" + type + "]", ANSI_CYAN) +
                    printColor(" =======", ANSI_BLUE));
        } catch (JMSException e) {}
    }


}
