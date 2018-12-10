package com.nowcoder.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ALTERUI on 2018/11/24 10:19
 */
@Service
public class SensitiveService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
    /**
     * 初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            //将敏感词文本加载进来
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            //一行一行的读取,然后将读取的内容赋值到lineTxt中
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String lineTxt;
            while ((lineTxt = reader.readLine()) != null) {
                //将读取的内容添加到敏感词树中
                addWord(lineTxt.trim());
            }

            reader.close();
        } catch (Exception e) {
            logger.error("敏感词文本读取失败" + e.getMessage());
        }


    }

    /**
     * 添加敏感词的树
     */
    //例如lineTxt为abc
    private void addWord(String lineTxt) {
        //当前节点为root节点
        TrieNode tempNode = rootNode;
        //遍历关键词，然后将关键词变成树
        for (int i = 0; i < lineTxt.length(); i++) {
            //读取当前字符
            char c = lineTxt.charAt(i);
            // 过滤空格
            if (isSymbol(c)) {
                continue;
            }
            //查看当前root节点的下一个节点
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {//表明没有该节点
                subNode = new TrieNode();
                //在当前节点下添加节点
                tempNode.addSubNode(c, subNode);
            }

            //然后将root节点指向当前节点
            tempNode = subNode;

            //判断是否为关键词的尾节点
            if (i == lineTxt.length() - 1) {
                tempNode.setKeyWordEnd(true);
            }

        }
    }



    /**
     * 创建一个trie,前缀树或者字典树
     */
    private class TrieNode {
        //是不是关键词结尾，默认false
        private boolean keyWordEnd = false;

        //当前节点下的所有节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        //添加节点
        public void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        //获取节点
        public TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        public boolean isKeyWordEnd() {
            return keyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            this.keyWordEnd = keyWordEnd;
        }

        //节点数目
        public int getNodeCounts() {
            return subNodes.size();
        }
    }

    //根节点
    private TrieNode rootNode = new TrieNode();


    /**
     * 将颜文字，非法字符过滤掉
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        //东亚文字0x2E80-0x9FFF
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    /**
     * 敏感词过滤器
     */
    public String filter(String text) {
        //先判断text是否为空，为空则直接返回
        if (StringUtils.isBlank(text)) {
            return text;
        }

        int begin = 0;//用于敏感词的定位
        int position = 0;//用于敏感词的查找
        TrieNode tempNode = rootNode;//根节点
        String sensitive= "***";//敏感词替代
        StringBuilder sb = new StringBuilder();//用户存放过滤后的text



        while (position < text.length()) {
            //获取当前位置的字符
            char c = text.charAt(position);
            //System.out.println(c);
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            //查询下一节点是否有c
             tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {//表明不是敏感词
                sb.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;


            } else if (tempNode.isKeyWordEnd()) {//表明找到一个敏感词了
                sb.append(sensitive);
                position ++;
                begin = position ;
                tempNode = rootNode;

            } else {
                position++;
            }



        }

        sb.append(text.substring(begin));
        return sb.toString();
    }





}
