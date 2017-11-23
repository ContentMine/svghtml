package org.xmlcml.graphics.svg.objects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGHTMLFixtures;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;


public class SVGArrowTest {

	private static final Logger LOG = Logger.getLogger(SVGArrowTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	// these files already have triangles
	@Test
	public void testArrowFactory() throws FileNotFoundException {
		SVGElement g = (SVGElement) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.OBJECTS_DIR, "arrows.svg")).getChildElements().get(0);
		List<SVGTriangle> triangleList = SVGTriangle.extractSelfAndDescendantTriangles(g);
		Assert.assertEquals(2,  triangleList.size());
		List<SVGLine> lineList = SVGLine.extractSelfAndDescendantLines(g);
		Assert.assertEquals(2,  lineList.size());
		ArrowFactory arrowFactory = new ArrowFactory();
		arrowFactory.readLinesTriangles(lineList, triangleList);
		arrowFactory.createFirstComeArrows();
		List<SVGArrow> arrowList = arrowFactory.getArrowList();
		Assert.assertEquals(2,  arrowList.size());
		SVGElement gg = new SVGG();
		for (int i = 0; i < arrowList.size(); i++) {
			gg.appendChild(arrowList.get(i));
			arrowList.get(i).setMarkerEndRef(SVGArrow.ARROWHEAD); 
			arrowList.get(i).setMarkerStartRef(SVGArrow.ARROWHEAD); 
			arrowList.get(i).setStroke("orange");
		}
		SVGSVG svg = new SVGSVG();
		svg.setMarker(SVGArrow.ARROWHEAD);
		svg.appendChild(gg);
		new File("target/arrows/").mkdirs();
		SVGUtil.debug(svg, new FileOutputStream("target/arrows/arrows0.svg"), 1);
	}

	@Test
	/** same but with markers packed.
	 * 
	 * @throws FileNotFoundException
	 */
	public void testArrowFactory1() throws FileNotFoundException {
		SVGElement g = (SVGElement) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.OBJECTS_DIR, "arrows.svg")).getChildElements().get(0);
		ArrowFactory arrowFactory = new ArrowFactory();
		arrowFactory.setMarkerEnd(SVGArrow.ARROWHEAD);
		arrowFactory.setStroke("red");
		arrowFactory.replaceLinesAndTrianglesByArrows(g);
		new File("target/arrows/").mkdirs();
		SVGSVG svg = new SVGSVG();
		svg.setMarker(SVGArrow.ARROWHEAD);
		g.detach();
		svg.appendChild(g);
		SVGUtil.debug(svg, new FileOutputStream("target/arrows/arrows1.svg"), 1);
	}
	
	@Test
	public void testMultiArrows() throws FileNotFoundException {
		SVGElement g = (SVGElement) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.OBJECTS_DIR, "multiTextBox.svg")).getChildElements().get(0);
		ArrowFactory arrowFactory = new ArrowFactory();
		arrowFactory.setMarkerEnd(SVGArrow.ARROWHEAD);
		arrowFactory.setStroke("orange");
		arrowFactory.replaceLinesAndTrianglesByArrows(g);
		Assert.assertEquals(29, arrowFactory.getArrowList().size());
		new File("target/arrows/").mkdirs();
		SVGSVG svg = new SVGSVG();
		svg.setMarker(SVGArrow.ARROWHEAD);
		g.detach();
		svg.appendChild(g);
		SVGUtil.debug(svg, new FileOutputStream("target/arrows/multiTextBox.svg"), 1);
	}
}
