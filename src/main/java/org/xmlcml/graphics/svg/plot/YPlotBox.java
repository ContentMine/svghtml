package org.xmlcml.graphics.svg.plot;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.cache.ComponentCache;
import org.xmlcml.graphics.svg.cache.LineCache;
import org.xmlcml.graphics.svg.cache.RectCache;
import org.xmlcml.graphics.svg.cache.ShapeCache;
import org.xmlcml.graphics.svg.cache.TextCache;

/** holds components of a y plot.
 * 
 * NOTE: "Y" means the numerical Y values are extracted and that the Y-axis needs scales
 * 
 * Currently barPlots being developed
 * creates axes from ticks, scales, titles.
 * 
 * @author pm286
 *
 */
public class YPlotBox extends AbstractPlotBox {
	
	public static final Logger LOG = Logger.getLogger(YPlotBox.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	static final String MINOR_CHAR = "i";
	static final String MAJOR_CHAR = "I";
	
	public YPlotBox() {
		setDefaults();
	}
	
	public void readAndCreateBarPlot(SVGElement svgElement) {
		componentCache = new ComponentCache(this);
		componentCache.setFileRoot(fileRoot);
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		// these need changing for bar plots
		RectCache rectCache = componentCache.getOrCreateRectCache();
		LOG.debug("rect "+rectCache);
		SVGElement rectCacheSVG = rectCache.getOrCreateConvertedSVGElement();
		SVGSVG.wrapAndWriteAsSVG(rectCacheSVG, new File("target/bar/rect.svg"));

		LineCache lineCache = componentCache.getOrCreateLineCache();
		LOG.debug("line "+lineCache);
		SVGElement lineCacheSVG = lineCache.getOrCreateConvertedSVGElement();
		SVGSVG.wrapAndWriteAsSVG(lineCacheSVG, new File("target/bar/line.svg"));
		
		TextCache textCache = componentCache.getOrCreateTextCache();
		LOG.debug("text "+textCache);
		SVGElement textCacheSVG = textCache.getOrCreateConvertedSVGElement();
		SVGSVG.wrapAndWriteAsSVG(textCacheSVG, new File("target/bar/text.svg"));
		List<SVGText> horTextList = textCache.getOrCreateHorizontalTexts();
		SVGSVG.wrapAndWriteAsSVG(horTextList, new File("target/bar/hortext.svg"));
		List<SVGText> verTextList = textCache.getOrCreateVerticalTexts();
		SVGSVG.wrapAndWriteAsSVG(verTextList, new File("target/bar/vertext.svg"));
//		SVGPhraseList phraseList = new SVG PhraseList();

		
		ShapeCache shapeCache = componentCache.getOrCreateShapeCache();
		SVGElement shapeCacheSVG = shapeCache.getOrCreateConvertedSVGElement();
		LOG.debug("shapes: "+shapeCache);
		SVGSVG.wrapAndWriteAsSVG(shapeCacheSVG, new File("target/bar/shape.svg"));
		
// axial polylines are converted to individual axes.

		List<SVGPolyline> polylineList = shapeCache.getPolylineList();
//		SVGSVG.wrapAndWriteAsSVG(polylineList, new File("target/bar/polyline.svg"));

		makeAxialTickBoxesAndPopulateContents();
		makeRangesForAxes();
		extractScaleTextsAndMakeScales();
		extractTitleTextsAndMakeTitles();
		extractDataScreenPoints();
		scaleXYDataPointsToValues();
		createCSVContent();
		writeProcessedSVG(svgOutFile);
		writeCSV(csvOutFile);
	}


	protected void extractDataScreenPoints() {
		screenYs = new RealArray();
//		for (SVGCircle circle : componentCache.getOrCreateShapeCache().getCircleList()) {
//			screenXYs.add(circle.getCXY());
//		}
//		if (screenXYs.size() == 0) {
//			LOG.trace("NO CIRCLES IN PLOT");
//		}
//		if (screenXYs.size() == 0) {
//			// this is really messy
//			for (SVGLine line : componentCache.getOrCreateShapeCache().getLineList()) {
//				Real2 vector = line.getEuclidLine().getVector();
//				double angle = vector.getAngle();
//				double length = vector.getLength();
//				if (length < 3.0) {
//					if (Real.isEqual(angle, 2.35, 0.03)) {
//						screenXYs.add(line.getMidPoint());
//					}
//				}
//			}
//		}
		screenYs.format(getNdecimal());
	}

	public void readAndCreateCSVPlot(SVGElement svgElement) {
		componentCache = new ComponentCache(this);
		componentCache.setFileRoot(fileRoot);
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		makeAxialTickBoxesAndPopulateContents();
		makeRangesForAxes();
		extractScaleTextsAndMakeScales();
		extractTitleTextsAndMakeTitles();
		extractDataScreenPoints();
		scaleXYDataPointsToValues();
		createCSVContent();
		writeProcessedSVG(svgOutFile);
		writeCSV(csvOutFile);
	}


	// graphics
	

	

	
	// getters and setters
	

	
	
	// static methods
	
	protected void scaleXYDataPointsToValues() {
		scaledYs = null;
		AnnotatedAxis xAxis = axisArray[AxisType.BOTTOM_AXIS];
		AnnotatedAxis yAxis = axisArray[AxisType.LEFT_AXIS];
		if (xAxis == null) {
			throw new RuntimeException("Missing xAxis");
		}
		if (yAxis == null) {
			throw new RuntimeException("Missing yAxis");
		}
		xAxis.ensureScales();
		yAxis.ensureScales();
		if (xAxis.getScreenToUserScale() == null ||
				xAxis.getScreenToUserConstant() == null ||
				yAxis.getScreenToUserScale() == null ||
				yAxis.getScreenToUserConstant() == null) {
			LOG.trace("XAXIS "+xAxis+"\n"+"YAXIS "+yAxis+"\n"+"Cannot get conversion constants: abort");
			return;
		}
	
		if (screenYs != null && screenYs.size() > 0) {
			scaledYs = new RealArray();
//			for (int i = 0; i < screenYs.size(); i++) {
//				double y = screenY;
//				double scaledY = yAxis.getScreenToUserScale() * y + yAxis.getScreenToUserConstant();
//				Real2 scaledXY = new Real2(scaledX, scaledY);
//				scaledXYs.add(scaledXY);
//			}
			scaledYs.format(ndecimal + 1);
		}
	}

	protected String createCSVContent() {
		// use CSVBuilder later
		StringBuilder sb = new StringBuilder();
		if (scaledYs != null) {
			for (int i = 0; i < scaledYs.size(); i++) {
				// this might be part of the axis...
				String xTitle = xTitles.get(i);
				
				double scaledY = scaledYs.get(i);
				sb.append(xTitle+","+scaledY+"\n");
			}
		}
		csvContent = sb.toString();
		return csvContent;
	}



}
