package org.xmlcml.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.plot.AbstractPlotBox;

/** superclass for caches.
 * 
 * @author pm286
 *
 */
public abstract class AbstractCache {
	
	private static final Logger LOG = Logger.getLogger(AbstractCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final double MARGIN = 1.0;
	
	protected Double axialEps = 0.1;
	protected Real2Range boundingBox;
	protected ComponentCache ownerComponentCache;
	protected Real2Range ownerComponentCacheBoundingBox;
	private AbstractPlotBox svgMediaBox;

	protected ShapeCache siblingShapeCache;
	

	protected AbstractCache() {
		
	}

	public AbstractCache(ComponentCache ownerComponentCache) {
		this.ownerComponentCache = ownerComponentCache;
		this.siblingShapeCache = ownerComponentCache == null ? null : ownerComponentCache.shapeCache;
		getOrCreateElementList();
	}

	public AbstractCache(AbstractPlotBox svgMediaBox) {
		this.svgMediaBox = svgMediaBox;
	}

	protected void drawBox(SVGElement g, String col, double width) {
		Real2Range box = this.getBoundingBox();
		if (box != null) {
			SVGRect boxRect = SVGRect.createFromReal2Range(box);
			boxRect.setStrokeWidth(width);
			boxRect.setStroke(col);
			boxRect.setOpacity(0.3);
			g.appendChild(boxRect);
		}
	}

	protected void writeDebug(String type, String outFilename, SVGG g) {
		File outFile = new File(outFilename);
		SVGSVG.wrapAndWriteAsSVG(g, outFile);
	}
	
	/** the bounding box of the cache
	 * 
	 * @return the bounding box of the containing svgCache (or null if none)
	 */
	public Real2Range getOrCreateComponentCacheBoundingBox() {
		if (ownerComponentCacheBoundingBox == null) {
			ownerComponentCacheBoundingBox = ownerComponentCache == null ? null : ownerComponentCache.getBoundingBox();
		}
		return ownerComponentCacheBoundingBox;
	}

	protected Real2Range getOrCreateBoundingBox(List<? extends SVGElement> elementList) {
		if (boundingBox == null) {
			boundingBox = (elementList == null || elementList.size() == 0) ? null :
			SVGElement.createBoundingBox(elementList);
		}
		return boundingBox;
	}

	/** the bounding box of the actual components
	 * The extent of the context (e.g. svgCache) may be larger
	 * @return the bounding box of the contained components
	 */
	public Real2Range getBoundingBox() {
		return getOrCreateBoundingBox(getOrCreateElementList());
	}
	
	public abstract List<? extends SVGElement> getOrCreateElementList();

	/** SVGG containing (copies of) all elements after processing.
	 * 
	 * @return
	 */
	public SVGG getOrCreateConvertedSVGElement() {
		SVGG svgg = new SVGG();
		List<? extends SVGElement> elementList = getOrCreateElementList();
		for (SVGElement component : elementList) {
			svgg.appendChild(component.copy());
		}
		return svgg;
	}

	public ComponentCache getOwnerComponentCache() {
		return ownerComponentCache;
	}

	public boolean remove(SVGElement element) {
		List<? extends SVGElement> elementList = this.getOrCreateElementList();
		boolean remove = elementList.remove(element);
		if (remove) {
			this.clearBoundingBoxToNull();
			ownerComponentCache.clearBoundingBoxToNull();
		}
		return remove;
	}
	
	/** clears bounding box to null.
	 * required after changes to contentCaches
	 */
	public void clearBoundingBoxToNull() {
		this.boundingBox = null;
	}

	public void superClearAll() {
		boundingBox = null;
		ownerComponentCacheBoundingBox = null;
	}
	
	public abstract void clearAll();

	public void setSiblingShapeCache(ShapeCache shapeCache) {
		this.siblingShapeCache = shapeCache;
	}
}
