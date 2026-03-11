package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {

    public static void main(String[] args) {
        // 1. 定义按键映射表和琴弦数组
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString[] strings = new GuitarString[37];

        // 2. 初始化 37 根琴弦，计算各自的频率
        for (int i = 0; i < 37; i++) {
            // 使用十二平均律的数学公式计算频率
            double frequency = 440.0 * Math.pow(2.0, (i - 24.0) / 12.0);
            strings[i] = new GuitarString(frequency);
        }

        // 3. 无限循环，监听用户操作并模拟物理引擎
        while (true) {
            // (a) 检测用户按键并拨动对应的琴弦
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);

                // 必须检查 index >= 0，防止用户按了映射表以外的键导致数组越界
                if (index >= 0) {
                    strings[index].pluck();
                }
            }

            // (b) 计算当前时刻所有琴弦的声波叠加 (Superposition)
            double sample = 0.0;
            for (int i = 0; i < 37; i++) {
                sample += strings[i].sample();
            }

            // (c) 播放合成后的声音
            StdAudio.play(sample);

            // (d) 让所有琴弦的时间步长向前推进 (Tic)
            for (int i = 0; i < 37; i++) {
                strings[i].tic();
            }
        }
    }
}