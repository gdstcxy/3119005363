package chachong;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.*;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySimHash {
    private String tokens; //字符串
    public BigInteger strSimHash;//字符产的hash值
    public int hashbits = 64; // 分词后的hash数;


    public MySimHash(String tokens)  {
        if(tokens.length()==0){
            throw  new Error("字符串为空");
        }
        this.tokens = tokens;
        this.strSimHash = this.simHash();
    }

    MySimHash(String tokens, int hashbits) {
        if(tokens.length()==0){
            throw  new Error("字符串为空");
        }
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.strSimHash = this.simHash();
    }


    /**
     * 清除html标签
     * @param content
     * @return
     */
    public String cleanResume(String content) {
        // 若输入为HTML,下面会过滤掉所有的HTML的tag
        content = Jsoup.clean(content, Whitelist.none());
        content = StringUtils.lowerCase(content);
        String[] strings = {" ", "\n", "\r", "\t", "\\r", "\\n", "\\t", "\\,","\\。","\\!","\\?","\\%","\\-","\\."};
        for (String s : strings) {
            content = content.replaceAll(s, "");
        }
        return content;
    }


    /**
     * 这个是对整个字符串进行hash计算
     * @return
     */
    private BigInteger simHash() {

        tokens = cleanResume(tokens); // cleanResume 删除一些特殊字符

        int[] v = new int[this.hashbits];

        List<Term> termList = StandardTokenizer.segment(this.tokens); // 对字符串进行分词

        //对分词的一些特殊处理 : 比如: 根据词性添加权重 , 过滤掉标点符号 , 过滤超频词汇等;
        Map<String, Integer> weightOfNature = new HashMap<String, Integer>(); // 词性的权重
        weightOfNature.put("n", 2); //给名词的权重是2;
        Map<String, String> stopNatures = new HashMap<String, String>();//停用的词性 如一些标点符号之类的;
        stopNatures.put("w", ""); //
        int overCount = 5; //设定超频词汇的界限 ;
        Map<String, Integer> wordCount = new HashMap<String, Integer>();

        for (Term term : termList) {
            String word = term.word; //分词字符串

            String nature = term.nature.toString(); // 分词属性;
            //  过滤超频词
            if (wordCount.containsKey(word)) {
                int count = wordCount.get(word);
                if (count > overCount) {
                    continue;
                }
                wordCount.put(word, count + 1);
            } else {
                wordCount.put(word, 1);
            }

            // 过滤停用词性
            if (stopNatures.containsKey(nature)) {
                continue;
            }

            // 2、将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
            BigInteger t = this.hash(word);
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
                int weight = 1;  //添加权重
                if (weightOfNature.containsKey(nature)) {
                    weight = weightOfNature.get(nature);
                }
                if (t.and(bitmask).signum() != 0) {
                    // 这里是计算整个文档的所有特征的向量和
                    v[i] += weight;
                } else {
                    v[i] -= weight;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < this.hashbits; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
            }
        }
        return fingerprint;
    }


    /**
     * 对单个的分词进行hash计算;
     * @param source
     * @return
     */
    public BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            /**
             * 当sourece 的长度过短，会导致hash算法失效，因此需要对过短的词补偿
             */
            while (source.length() < 3) {
                source = source + source.charAt(0);
            }
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    /**
     * 计算海明距离,海明距离越小说明越相似;
     * @param other
     * @return
     */
    public int hammingDistance(MySimHash other) {
        BigInteger m = new BigInteger("1").shiftLeft(this.hashbits).subtract(
                new BigInteger("1"));
        BigInteger x = this.strSimHash.xor(other.strSimHash).and(m);
        int tot = 0;
        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }


    public double getSemblance(MySimHash s2 ){
        double i = (double) this.hammingDistance(s2);
        return 1 - i/this.hashbits ;
    }

    public static void main(String[] args) throws IOException {
        //System.out.println("Your first argument is: " + args[0] +args[1]);
        if(args.length!=3){
            if(args.length<3){
                throw new Error("输入的文件路径数少于三个");
            }
            else{
                throw new Error("输入的文件路径数大于三个");
            }
        }


        String f1 = args[0];
        String f2 =args[1];
        DecimalFormat df = new DecimalFormat(".00");  //结果保留两位
        String rf1 = readfile(f1);
        String rf2 = readfile(f2);
        long l1 = System.currentTimeMillis();
        MySimHash hash1 = new MySimHash(rf1, 64);
        MySimHash hash2 = new MySimHash(rf2, 64);

        String semblance = df.format(hash1.getSemblance(hash2) * 100)+"%";
      /*  System.out.println(  "汉明距离："+hash1.hammingDistance(hash2) );

        System.out.println(  "相似度  ："+semblance );

       */
        long l2 = System.currentTimeMillis();
        System.out.println(l2-l1 +"ms");

        //将结果写入文件
        File f =new File(args[2]);
        String parent = f.getParent();

        File resultpath =new File(parent);
        if(!resultpath.exists()){
            resultpath.mkdirs();
        }
        File resultfile =new File(args[2]);
        if(!resultfile.exists()){
            resultfile.createNewFile();
            System.out.println("'文件创建成功" );
        }
        BufferedWriter BW =new BufferedWriter(new FileWriter(resultfile));
        BW.write("原文文件："+f1+"\r\n");
        BW.write("抄袭版论文的文件："+f2+"\r\n");
        BW.write("相似度："+semblance+"\r\n");
        BW.flush();
        BW.close();

        System.out.println("结果已存入."+args[2]+"中");

    }
    //读取文件
    public static String readfile(String f1) {
        BufferedReader br = null;
        StringBuffer sb = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f1), "UTF-8")); //这里可以控制编码
            sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                 sb.append("\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new String(sb);
    }


}
