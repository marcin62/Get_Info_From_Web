import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class Task2 {
	public static void main(String[] args) {
		 
		   Put put=new Put();
		   put.run(args); 
		   
	}	
}

class Put {
	
	Getter getter;
	
	Put()
	{
		getter= new Getter();
	}
	
	void run(String[] link)
	{
		if(link.length!=1){
			System.out.println("Z³a liczba argumentow");
		}else {
		link(link[0]);
		emails(link[0]);
		try {
			info(link[0]);
		} catch (IOException e) {
			System.out.println("Nie uda³o siê poprawnie zapisaæ informacji o po³¹czeniu");
		}
		finally {
			System.out.println("Program zakoñczy³ dzia³anie");
		}
		}
	}
	
	void link(String link)
	{
		Set<String> linki=getter.getLinks(link);
	    try {
	    	save(linki,"linki.txt");
	    } catch (Exception e) {
	    System.out.println("Wyst¹pi³ b³¹d w pliku linki.txt");
	    }
	}
	
	void emails(String link)
	{
		Set<String> email=getter.getEmails(link);
	    try {
	    	save(email,"email.txt");
	    } catch (Exception e) {
		System.out.println("Wyst¹pi³ b³¹d w pliku email.txt");
	    }
	}
	
	void info(String link) throws IOException 
	{
		Set<String> info=new HashSet<String>();
        InetAddress address = InetAddress.getByName(new URL(link).getHost());
        info.add("Name: " + address.getHostName());
        info.add("Addr: " + address.getHostAddress());
        info=getter.getHeaders(link,info);
        try {
		    save(info,"info.txt");
		} catch (Exception e) {
			System.out.println("Wyst¹pi³ b³¹d w pliku info.txt");
		}
	}

	static void save(Set<String> obj, String path) throws Exception {
	    PrintWriter pw = null;
	    try {
	        pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
	        for (String s : obj) {
	            pw.println(s);
	        }
	        pw.flush();
	    } finally {
	        pw.close();
	    }
	}
}

class Getter{
	private Pattern htmltag;
	private Pattern link;

	public Getter() {
	    htmltag = Pattern.compile("<a\\b[^>]*href=\"[^>]*>(.*?)</a>");
	    link = Pattern.compile("href=\"[^>]*\">");
	}    
	
    public Set<String> getLinks(String url){
    	Set<String> links = new HashSet<String>();
        try {
            StringBuilder builder = connect(url);

            Matcher tagmatch = htmltag.matcher(builder.toString());
            while (tagmatch.find()) {
                Matcher matcher = link.matcher(tagmatch.group());
                if(matcher.find()) {
                String link2 = matcher.group().replaceFirst("href=\"", "")
                        .replaceFirst("\">", "")
                        .replaceFirst("\"[\\s]?target=\"[a-zA-Z_0-9]*", "");
                if (valid(link2)) {
                	if(link2.contains("class"))
                	{
                		String []temp=link2.split("\"");
                		link2=temp[0];
                	}
                	if(link2.length()>5&&link2.contains("htt"))
                		links.add(link2);
                }
                }
            }
        } catch (IOException e) {
            System.out.println("Pojawi³ siê problem z odczytaniem linków");
        }
        return links;
    }
    
    public Set<String> getEmails(String url){
		Set<String> links = new HashSet<String>();
		try
		{
	    StringBuilder builder = connect(url);
        String inputLine=builder.toString();
            
        Pattern pattern = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}", Pattern.CASE_INSENSITIVE);
        Matcher matchs = pattern.matcher(inputLine);
        while (matchs.find()) {
            		links.add(matchs.group());
        }
		}
		catch(IOException a)
		{
			System.out.println("Pojawi³ siê problem z odczytaniem emaili");
		}
	 return links;
    }
    
    public Set<String> getHeaders(String www,Set<String> info)  {
    	try {
        URL url = new URL(www);
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
 
        Map hdrs = httpCon.getHeaderFields();
        Set hdrKeys = hdrs.keySet();
 
        for (Object k : hdrKeys)
        	info.add("Key: " + k + "  Value: " + hdrs.get(k));
    	}catch(IOException e)
    	{
    		System.out.println("Pojawi³ siê problem z odczytaniem informacji");
    	}
        return info;
    }

	StringBuilder connect(String url) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
        String s;
        StringBuilder builder = new StringBuilder();
        while ((s = bufferedReader.readLine()) != null) {
            builder.append(s);
        }
		return builder;
	}
    
    private boolean valid(String s) {
        if (s.matches("javascript:.*|mailto:.*")) {
            return false;
        }
        return true;
    }
}