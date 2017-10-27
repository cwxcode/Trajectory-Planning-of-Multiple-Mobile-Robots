package cz.agents.alite.communication.content.binary;

import cz.agents.alite.communication.content.Content;

/**
 * Content type which can be easily sent over the wire
 * 
 * @author stolba
 *
 */
public class BinaryContent extends Content {

	private static final long serialVersionUID = 4073007198209901737L;

	public BinaryContent(byte[] data) {
		super(data);
	}
	
	@Override
	public byte[] getData() {
        return (byte[])super.getData();
    }

}
