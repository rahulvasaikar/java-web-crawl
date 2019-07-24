package com.eulerity.hackathon;

import java.io.IOException;
//import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet(
    name = "HelloAppEngine",
    urlPatterns = {"/hello"}
)
public class HelloAppEngine extends HttpServlet{

	private static final long serialVersionUID = 1L;

	protected static final Gson GSON = new GsonBuilder().create();

	private static HashSet<String> imageslinks, logolinks, links;
	private static final int MAX_DEPTH = 2;
	//private String testImages[] = {"https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/image/AppleInc/aos/published/images/w/at/watch/shop/watch-shop-series4-logo-201809-grid_GEO_US?wid=120&hei=47&fmt=png-alpha&.v=1544052663425","https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/image/AppleInc/aos/published/images/w/at/watch/modelheader/watch-modelheader-hermes-logo-201809-grid?wid=258&hei=42&fmt=png-alpha&.v=1544052638340","https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/image/AppleInc/aos/published/images/w/at/watch/modelheader/watch-modelheader-nike-logo-201809-grid_GEO_US?wid=211&hei=42&fmt=png-alpha&.v=1544052655849"};
	
	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/json");
		String servlet = req.getServletPath();
		if(servlet.equalsIgnoreCase("/main")){
				String urlString = req.getParameter("url");
				new HelloAppEngine().getPageLinks(urlString, 0);
				
				resp.getWriter().println(GSON.toJson(logolinks));
				System.out.println("total images: "+logolinks);
				//resp.getWriter().println(GSON1.toJson(testImages));

				System.out.println("hello "+imageslinks);
		}
	}

    public HelloAppEngine() {
        links = new HashSet<>();
        imageslinks = new HashSet<>();
        logolinks = new HashSet<>();
    }

    public void getPageLinks(String URL, int depth) {
        if ((!links.contains(URL) && (depth < MAX_DEPTH))) {
            System.out.println(">> Depth: " + depth + " [" + URL + "]");
            try {
                links.add(URL);
                Document document = Jsoup.connect(URL).get();
                Elements img = document.getElementsByTag("img");
                
                for (Element el : img) { 
                    String src = el.absUrl("src"); 
                    if (src.toLowerCase().contains("logo"))
                    	logolinks.add(src);
                    else
                    	imageslinks.add(src);    
                }
                
                Elements linksOnPage = document.select("a[href]");
                depth++;
                for (Element page : linksOnPage) {
                    getPageLinks(page.attr("abs:href"), depth);
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }
}

