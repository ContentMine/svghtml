package org.xmlcml.graphics.svg.text.build;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.rule.horizontal.LineChunk;

import nu.xom.Element;

public class BlankNew extends LineChunk {

	private static final Logger LOG = Logger.getLogger(BlankNew.class);
	public final static String TAG = "blank";
	
	private Real2Range boundingBox;

	public BlankNew(Real2Range bbox) {
		super();
		this.setClassName(TAG);
		this.boundingBox = bbox;
	}

	@Override
	public String toString() {
		return "Blank: "+boundingBox.toString();
	}

	public Element copyElement() {
		return (Element) this.copy();
	}

	protected List<? extends LineChunk> getChildChunks() {
		throw new RuntimeException("not applicable");
	}

}