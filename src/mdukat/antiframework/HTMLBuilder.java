package mdukat.antiframework;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Scanner;

public class HTMLBuilder {

    private String templateFilePath;
    private String templateFile;

    public HTMLBuilder(String templateFilePath){
        readHTMLFile(templateFilePath);
    }

    // Reads template file, compiles it, or builds error message
    public HTMLBuilder(String templateFilePath, Map<String, String> parseKeys){

        readHTMLFile(templateFilePath);

        // For every key, replace value
        for(Map.Entry<String, String> entry : parseKeys.entrySet()){
            this.templateFile = this.templateFile.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
        }
    }

    private void readHTMLFile(String templateFilePath){
        this.templateFilePath = templateFilePath;

        Scanner in;
        try {
            in = new Scanner(new FileReader(this.templateFilePath));
        } catch(FileNotFoundException e){
            in = new Scanner("Template file not found: " + this.getTemplateFilePath());
        } catch(Exception e){
            in = new Scanner("Undefined HTMLBuilder error");
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        while(in.hasNextLine()){
            sb.append(in.nextLine());
        }
        in.close();

        this.templateFile = sb.toString();
    }

    // Returns raw compiled HTML (or error message)
    public String getHTML(){
        return this.templateFile;
    }

    public String getTemplateFilePath(){
        return this.templateFilePath;
    }
}
