package net.yxy.dagger.html.analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class HtmlElement {
	private Element element ;
	
	public HtmlElement(Element elem) {
		element = elem ;
	}
	
	public String text() {
		final StringBuilder accum = new StringBuilder();
        new NodeTraversor(new NodeVisitor() {
            public void head(Node node, int depth) {
                if (node instanceof TextNode) {
                    TextNode textNode = (TextNode) node;
                    //element.appendNormalisedText(accum, textNode);
					try {
						Method method = element.getClass().getDeclaredMethod("appendNormalisedText", new Class[]{StringBuilder.class, TextNode.class});
						method.setAccessible(true);
						method.invoke(null, new Object[]{accum, textNode}); 
						method.setAccessible(false);
					} catch (Exception e) {
						e.printStackTrace();
					} 

                } else if (node instanceof Element) {
                    Element element = (Element) node;
                	try {
                		Field field=element.getClass().getDeclaredField("tag");
                		field.setAccessible(true);
                		Tag tag = (Tag) field.get(element) ;
                		field.setAccessible(false);
						Method method = TextNode.class.getDeclaredMethod("lastCharIsWhitespace", new Class[]{StringBuilder.class});
						method.setAccessible(true);
						 if (accum.length() > 0 &&
			                        (element.isBlock() || tag.getName().equals("br")) &&
			                        !(boolean)method.invoke(null, new Object[]{accum})){
							 accum.append(" ");
						 }
						 method.setAccessible(false);
					} catch (Exception e) {
						e.printStackTrace();
					} 

                   
                }
            }
            
            private boolean isCommaAdded = false ;
            
            public void tail(Node node, int depth) {
            	if (node instanceof Element){
            		int endIdx = accum.length()-1 ;
            		while(endIdx>0 && accum.charAt(endIdx)==' '){
            			endIdx-- ;
            		}
            		if(endIdx>=0){
            			char lastChar = accum.charAt(endIdx) ;
                		if(lastChar == ',' || lastChar == '.'){
                			return ;
                		}
                		Element element = (Element) node;
                		String nodeTagName = null ;
                		
                		try{
	                		Field field=element.getClass().getDeclaredField("tag");
	                		field.setAccessible(true);
	                		Tag tag = (Tag) field.get(element) ;
	                		field.setAccessible(false);
	                		nodeTagName = tag.getName();
                		} catch (Exception e){
                			e.printStackTrace();
                		}
                		
                		if(isCommaAdded==false && (nodeTagName.equalsIgnoreCase("td") || nodeTagName.equalsIgnoreCase("li"))){
                			accum.append(",") ;
//                			isCommaAdded = true ;
                		}else if(nodeTagName.equalsIgnoreCase("tr")){
                			accum.append(".") ;
                		}
            		}
            		
            	}
            }
        }).traverse(element);
        return accum.toString().trim();
	}

}
