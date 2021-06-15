/*
 * Copyright © 2021 Mr.Po (ldd_live@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pomo.hangman;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <h2>柜子上游戏 命令行</h2>
 *
 * <p>可通过向控制台输入特定指令，操作游戏。</p>
 *
 * @author Mr.Po, ldd_live@foxmail.com
 */
public class HangManCMD {

    private final BlockingQueue<Character> input = new LinkedBlockingQueue<>(1);

    private enum State {
        VALID,
        ERROR,
        EXIST
    }

    public static void main(String[] args) throws InterruptedException {

        HangManCMD command = new HangManCMD();
        command.start();
    }

    /**
     * 开始游戏
     */
    private void start() throws InterruptedException {

        HangManGUI.Listener listener = HangManGUI.start(this);

        final Random random = new Random();
        final String[] wordList = getWordList();

        init();

        try {

            do {

                int index = random.nextInt(wordList.length);

                final String word = wordList[index];
                final char[] words = word.toCharArray();
                final char[] masks = word.replaceAll("\\w", "*").toCharArray();

                int errorCount = 0;
                String mask = String.valueOf(masks);

                listener.onReset(mask);

                while (errorCount < 7) {

                    System.out.printf("<Guess> Enter a letter in word %s >", mask);

                    char next = next();

                    State state = check(words, masks, next);

                    switch (state) {

                        case VALID:

                            mask = String.valueOf(masks);
                            listener.onValid(mask);

                            break;
                        case ERROR:

                            System.out.printf("\t\t %s is not in the word%n", next);

                            errorCount++;
                            listener.onError(errorCount, mask, next);

                            break;
                        case EXIST:

                            System.out.printf("\t\t %s is already in the word%n", next);
                            break;
                    }

                    if (mask.equals(word)) break;
                }

                listener.onFinish(word);

                System.out.printf("The word is %s. You missed %s times%n", word, errorCount);


            } while (question());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listener.onDestroy();
    }

    /**
     * <h2>得到单词数组</h2>
     *
     * @return 单词数组
     */
    private String[] getWordList() {

        File file = new File("wordlist.txt");

        // 文件存在
        if (file.exists() && file.length() > 0) {

            try {

                String[] wordlist = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                        .limit(1000)
                        .filter(it -> !it.trim().isEmpty())
                        .toArray(String[]::new);

                if (wordlist.length >= 3) {

                    System.out.printf("use external wordlist.[%s]%n", wordlist.length);

                    return wordlist;
                }

            } catch (Exception e) {

                System.out.printf("%s read fail.%n", file.getAbsolutePath());
            }

        } else System.out.printf("not found valid wordlist.(%s)%n", file.getAbsolutePath());

        String[] wordlist = {"receive", "write", "read", "delete", "question", "problem"};

        System.out.printf("use default wordlist.[%s]%n", wordlist.length);

        return wordlist;
    }

    private void init() {

        Thread thread = new Thread(() -> {

            Scanner scanner = new Scanner(System.in);

            try {

                while (true) {

                    String next = scanner.nextLine();

                    if (next.length() > 0) {
                        input.put(next.charAt(0));
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * <h2>询问是否进行下一场游戏</h2>
     *
     * @return 是/否
     */
    private boolean question() throws InterruptedException {

        while (true) {

            System.out.print("Do you want to guess for another word? Enter y or n >");

            char next = next();

            if (next == 'n') {

                return false;

            } else if (next == 'y') {

                return true;

            } else {

                System.out.println("Wrong type");
            }
        }
    }

    public void input(char next) {
        try {
            input.put(next);
            System.out.println(next);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * <h2>输入</h2>
     *
     * @return 本次输入内容
     */
    private char next() throws InterruptedException {
        return input.take();
    }

    /**
     * <h2>检查输入是否有效</h2>
     *
     * @param words 单词字符数组
     * @param masks 星号字符数组
     * @param next  待判断字符
     * @return 结果
     */
    private State check(char[] words, char[] masks, char next) {

        State state = State.ERROR;

        for (int i = 0; i < words.length; i++) {

            if (words[i] == next) {

                if (masks[i] == next) return State.EXIST;

                masks[i] = next;

                state = State.VALID;
            }
        }

        return state;
    }
}
