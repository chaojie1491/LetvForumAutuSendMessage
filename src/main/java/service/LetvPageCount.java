package service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LetvPageCount implements PageCountAdapter {

	Logger Logger = LoggerFactory.getLogger(LetvPageCount.class);

	@Override
	public int getPageCount(Document document) {
		Element countTagElement = document.select("a.last").get(0);
		String lasgPageString = countTagElement.attr("href");
		String pageCountString = lasgPageString
				.substring(lasgPageString.lastIndexOf("-") + 1, lasgPageString.lastIndexOf(".")).trim();
//		Logger.info("页数:" + pageCountString);
		return Integer.valueOf(pageCountString);
	}

}
