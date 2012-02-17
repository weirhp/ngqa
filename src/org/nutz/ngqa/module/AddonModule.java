package org.nutz.ngqa.module;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.Tag;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

@IocBean
@InjectName
@Ok("void")
@Fail("http:500")
@Filters()
public class AddonModule {

	@At("/sitemap")
	@Ok("void")
	public void sitemap(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		DBCursor cur = questionColl.find(new BasicDBObject(), new BasicDBObject("updateAt", 1)).sort(new BasicDBObject("createAt", -1));
		String urlRoot = "http://" + req.getHeader("Host") + req.getSession().getServletContext().getContextPath() + "/question";
		Tag urlset = Tag.tag("urlset");
		urlset.attr("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9")
		   .attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
		   .attr("xsi:schemaLocation", "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd");
		
		urlset.add("url").add("loc").setText("http://" + req.getHeader("Host"));
		
		while(cur.hasNext())
			urlset.add("url").add("loc").setText(urlRoot + cur.next().get("_id") + ".shtml");
		
		Writer writer = resp.getWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		writer.write(urlset.toString());
	}
	
	@Inject("java:$commons.coll('question')")
	private DBCollection questionColl;
}
