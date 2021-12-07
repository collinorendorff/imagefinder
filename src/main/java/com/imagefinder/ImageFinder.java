package com.imagefinder;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection;
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
    name = "ImageFinder",
    urlPatterns = {"/main"}
)
public class ImageFinder extends HttpServlet{
	
		//1st parameter layers: How many "layers" of the website to go into; the first if statement will determine
		//		how many layers are iterated. If only 1 layer is iterated, this function (via the helper function)
		//		will only populate "visited" all of the links found on the first page. If 2 layers are iterated, it will 
		//		return all of the links on the first page, as well as all of the links on those pages, and so forth.
		//2nd parameter url: This is the link that we will navigate to to pull further urls from the same domain.
		//3rd parameter visited: This will keep track of urls that were already visited so that we are not crawling the same webpage twice.
		private static void crawl(int layers, String url, ArrayList<String> visited) {
			//the value of the int being compared to layers directly below can be increased to crawl further through the domain to 
			//subsequent webpages
			//		**note that increasing this value will exponentially increase the running time of the program**
			if(layers <= 1) {
				Document doc = request(url, visited);
				
				if(doc != null) {
					for(Element link : doc.select("a[href]")) {
						String next_link = link.absUrl("href");
						if(visited.contains(next_link) == false) {
							crawl(layers++, next_link, visited);
						}
					}
				}
			}
		}
		
		//helper function to request access to the url; try-catch statement to make sure that it successfully
		//		connects; takes in an ArrayList v to which it will add the url if it has not been previously
		//		visited (checked in crawl function above) to ensure the same page is not visited twice
		private static Document request(String url, ArrayList<String> v) {
			try {
				Connection con = Jsoup.connect(url);
				Document doc = con.get();
				
				//200 status code will show that a proper connection has been made
				if(con.response().statusCode() == 200) {
					v.add(url);
					
					return doc;
				}
				return null;
			}
			catch(IOException e) {
				return null;
			}
		}
	
	private static final long serialVersionUID = 1L;

	protected static final Gson GSON = new GsonBuilder().create();
	
	//"webpages" will hold all of the webpage url's that are to be visited and crawled for images
	//"images" will contain the links to all of the images to be printed on the localhost:8080 site
	ArrayList<String> webpages = new ArrayList<String>();
	ArrayList<String> images = new ArrayList<String>();

	//This is just a test array
	/*public static final String[] testImages = {
			"https://images.pexels.com/photos/545063/pexels-photo-545063.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/464664/pexels-photo-464664.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/406014/pexels-photo-406014.jpeg?auto=compress&format=tiny",
			"https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg?auto=compress&format=tiny"
  };*/

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/json");
		String path = req.getServletPath();
		String url = req.getParameter("url");
		System.out.println("Got request of: " + path + " with query param: " + url);
		
		//adding initial user-entered url into webpages ArrayList to make sure we get images from that webpage, along with the others
		webpages.add(url);
		
		//this recursive function call will populate the "webpages" ArrayList with links from the webpage of the user-provided url
		crawl(1, url, webpages);
		
		//This segment of code will connect to the url before looping, looking into the HTML source code to find the logo/favicon of the
		//		domain. It will add it to the images ArrayList first; thus, the first image printed after every user input will be the
		//		logo/favicon of the domain.
		Document doc = Jsoup.connect(url).get();
		Element logo = doc.head().select("link[href~=.*\\.(ico|png)]").first();
		if(logo != null) {
			images.add(logo.attr("href"));
		}
		
		//this for loop will iterate through the "webpages" ArrayList; it will connect to them, filter out the elements by the "img" tag
		//		(to make sure that it is getting the url to an image), then add the link to the "images" ArrayList
		for(int i = 0; i < webpages.size(); i++) {
			doc = Jsoup.connect(webpages.get(i)).get();
			Elements pics = doc.getElementsByTag("img");
			for(Element src : pics) {
				//this if statement makes sure that we are not adding the link to the same picture twice; if this is not important
				//		then this if statement can be removed
				if(images.contains(src.attr("abs:src")) == false) {

					images.add(src.attr("abs:src"));
				}
			}
		}
		
		resp.getWriter().print(GSON.toJson(images));
		
		//after printing out the pictures onto the site, the two ArrayLists are cleared so that if the user enters another url,
		//		they are not "added onto the end" of those from the last url
		webpages.clear();
		images.clear();
	}
}
