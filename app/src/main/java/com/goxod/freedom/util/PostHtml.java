package com.goxod.freedom.util;

/**
 * Created by Levey on 2016/3/11.
 */
public class PostHtml {
    public static String getHtml(String main){
        String head = "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "    <title>Freedom</title>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,maximum-scale=4.0,user-scalable=1\" />\n" +
                "    <meta name=\"format-detection\" content=\"telephone=no\" />\n" +
                "    <meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />\n" +
                "\t<link rel=\"stylesheet\" href=\"file:///android_asset/style.css\" type=\"text/css\" />\n" +
                "</head><body>\n<div id=\"main\">\n";
        String foot = "</div></body></html>";
        return head + main + foot;
    }
}
