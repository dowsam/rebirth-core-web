/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-core GMailEngine.java 2012-7-19 16:20:55 l.xue.nong$$
 */
package cn.com.rebirth.core.web.jcaptcha;

import java.awt.Font;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.FunkyBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomRangeColorGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.RandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;

/**
 * The Class GMailEngine.
 *
 * @author l.xue.nong
 */
public class GMailEngine extends ListImageCaptchaEngine {

	/* (non-Javadoc)
	 * @see com.octo.captcha.engine.image.ListImageCaptchaEngine#buildInitialFactories()
	 */
	@Override
	protected void buildInitialFactories() {
		WordGenerator wgen = new RandomWordGenerator("abcdefghijklmnopquvwxyz123456789");
		RandomRangeColorGenerator cgen = new RandomRangeColorGenerator(new int[] { 0, 100 }, new int[] { 0, 100 },
				new int[] { 0, 100 });
		//文字显示4个数
		TextPaster textPaster = new RandomTextPaster(new Integer(5), new Integer(5), cgen, true);
		//图片的大小
		BackgroundGenerator backgroundGenerator = new FunkyBackgroundGenerator(new Integer(200), new Integer(45));

		Font[] fontsList = new Font[] { new Font("Arial", 0, 10), new Font("Tahoma", 0, 10),
				new Font("Verdana", 0, 10), };

		FontGenerator fontGenerator = new RandomFontGenerator(new Integer(20), new Integer(30), fontsList);

		WordToImage wordToImage = new ComposedWordToImage(fontGenerator, backgroundGenerator, textPaster);
		this.addFactory(new GimpyFactory(wgen, wordToImage));

	}

}
