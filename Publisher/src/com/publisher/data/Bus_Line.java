package com.publisher.data;

import org.apache.commons.codec.digest.DigestUtils;

import com.publisher.Global;

public class Bus_Line
{
	public int		m_nLineCode;
	public int		m_nLineId;
	public String	m_strDescription;
	public String   m_strKey;		//the sha1key of his line code
	public Bus_Line(String strLine)
	{
		String[] arrInfo = strLine.split(",");
		if( arrInfo.length != 3 )
			return;
		
		m_nLineCode = Integer.valueOf(arrInfo[0].trim());
		m_nLineId= Integer.valueOf(arrInfo[1].trim());
		m_strDescription= arrInfo[2];
		
		byte[] arrKey = DigestUtils.sha1(" " + m_nLineCode );
		m_strKey = Global.byteArrayToHexString(arrKey);
	}
}