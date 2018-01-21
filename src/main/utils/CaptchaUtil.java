package utils;

import javax.servlet.http.HttpServletRequest;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * 验证码工具类
 *
 * @author 廿二月的天
 */
public class CaptchaUtil {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 38;
    private static final int CODE_LENGTH = 4;
    private static final String RANDOM_STRING = "ABCDEFGHIJKLMNPQRSTUVWXYZ1234567890abcdefghijkmnpqrstuvwxyz";
    private static final String SESSION_KEY = "CAPTCHA";
    private static final String FONT_NAME = "Times New Roman";
    private static final int FONT_SIZE = 24;

    private CaptchaUtil() {
    }

    /**
     * 获取验证码
     *
     * @param request HTTP请求对象
     * @return 验证码图片缓存流
     */
    public static BufferedImage getImage(HttpServletRequest request) {
        // 在内存中创建图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        // 获取图片上下文
        Graphics g = image.getGraphics();
        // 生成随机类
        Random random = new Random();
        // 设定背景颜色
        g.setColor(getRandColor(100, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // 设定字体
        g.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, xl, y + yl);
        }
        // 取随机产生的认证码
        String sRand = randomRand(CODE_LENGTH);
        int strWidth = WIDTH / 2 - g.getFontMetrics().stringWidth(sRand) / CODE_LENGTH - FONT_SIZE;
        int strHeight = HEIGHT / 2 + g.getFontMetrics().getHeight() / 4;
        for (int i = 0; i < CODE_LENGTH; i++) {
            String rand = sRand.substring(i, i + 1);
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6 + strWidth, strHeight);
        }
        request.getSession().setAttribute(SESSION_KEY, sRand);
        request.setAttribute("sRand", sRand);
        g.dispose();
        return image;
    }

    /**
     * 随机结果数
     *
     * @param length 随机结果的长度
     * @return 随机结果字符串
     */
    public static String randomResult(int length) {
        String[] i = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        List<String> l = new ArrayList<>(Arrays.asList(i));
        Random ran = new Random();
        StringBuilder s = new StringBuilder();
        while (l.size() > 10 - length) {
            s.append(l.remove(ran.nextInt(l.size())));
        }
        s = new StringBuilder(s.toString().replaceAll("^(0)(\\d)", "$2$1"));
        return s.toString();
    }

    /**
     * 给定范围获取随机颜色
     *
     * @param fc 前景色数值
     * @param bc 背景色数值
     * @return 随机颜色对象
     */
    private static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 生成随机验证码文字
     *
     * @param n 验证码文字个数
     * @return 验证码文字
     */
    private static String randomRand(int n) {
        StringBuilder rand = new StringBuilder();
        int len = RANDOM_STRING.length() - 1;
        double r;
        for (int i = 0; i < n; i++) {
            r = ((Math.random())) * len;
            rand.append(RANDOM_STRING.charAt((int) r));
        }
        return rand.toString();
    }
}
