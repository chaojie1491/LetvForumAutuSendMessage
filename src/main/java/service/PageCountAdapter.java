package service;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface PageCountAdapter{

	int getPageCount(Document document) throws IOException;
	
}
