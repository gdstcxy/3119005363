package chachong;

import org.junit.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public class MySimHashTest {

    private static MySimHash a = new MySimHash("《活着》是作家余华的代表作之一", 64);
    private static MySimHash b = new MySimHash("《活着》是作家余华的代表作之一", 64);
    private static MySimHash c = new MySimHash("《 活 着 》 是 作 家 余 华 的 代 表 作 之 一", 64);
    private static MySimHash d = new MySimHash("《余华》的代表作之一是作家活着", 64);

    //java.lang.NullPointerException
    // private  static MySimHash e = new MySimHash(null,64);

    //java.lang.Error: 字符串为空
    //  private  static MySimHash f = new MySimHash("",64);
    private static MySimHash g = new MySimHash("《活着》的代表作", 64);
    private static MySimHash h = new MySimHash("《活着》是作家余华的代表作之一，这本书讲述了", 64);

    //异常路径
    String[] args0 = {" ", " "};
    String[] args00 = {
            "D:\\tests\\orig.txt",
            "D:\\tests\\orig_0.8_add.txt",
            "D:\\tests\\orig_0.8_dis_15.txt",
            "D:\\tests\\ans.txt",
    };
    //正常路径
    String[] args1 = {
            "D:\\tests\\orig.txt",
            "D:\\tests\\orig_0.8_add.txt",
            "D:\\tests\\ans1.txt",
    };
    String[] args2 = {
            "D:\\tests\\orig.txt",
            "D:\\tests\\orig_0.8_del.txt",
            "D:\\tests\\ans2.txt",
    };
    String[] args3 = {
            "D:\\tests\\orig.txt",
            "D:\\tests\\orig_0.8_dis_1.txt",
            "D:\\tests\\ans3.txt",
    };
    String[] args4 = {
            "D:\\tests\\orig.txt",
            "D:\\tests\\orig_0.8_dis_10.txt",
            "D:\\tests\\ans4.txt",
    };
    String[] args5 = {
            "D:\\tests\\orig.txt",
            "D:\\tests\\orig_0.8_dis_15.txt",
            "D:\\tests\\ans5.txt",
    };

    @Before
    public void setUp() {
        System.out.println("测试开始");
    }

    @After
    public void tearDown() {
        System.out.println("测试结束");
    }

    @Test
    public void testgetSemblance() {
        System.out.println("这是对相似度的测试");
        System.out.println("相同字符串的相似度为：" + a.getSemblance(b));
        System.out.println("用空格分隔的字符串相似度为：" + a.getSemblance(c));
        System.out.println("乱序的字符串的相似度为:" + a.getSemblance(d));
        //System.out.println(a.getSemblance(e)); //字符串为null
        //System.out.println(a.getSemblance(f));   //字符串内容为空
        System.out.println("子串的相似度为:" + a.getSemblance(g));
        System.out.println("抄袭版本比原版本长的相似度:" + a.getSemblance(h));
        //  System.out.println("字符串哈希值:"+a.strSimHash);
        //System.out.println(a.cleanResume("《活着》是作家余华的代表作之一"));
        //
        // System.out.println("海明距离:"+a.hammingDistance(b));

        // MySimHash.readfile("");java.io.FileNotFoundException:
        //   MySimHash.readfile("E:");java.io.FileNotFoundException: E: (系统找不到指定的路径。)
        // MySimHash.readfile("D:\\tests\\orig.txt");
    }

    @Test
    public void testreadfile() {
        System.out.println("这是对读取文件的测试");
        //正常路径测试
        //System.out.println(MySimHash.readfile("D:\\tests\\orig.txt"));
        //异常路径测试
        //  System.out.println(MySimHash.readfile("E:"));//java.io.FileNotFoundException:  (系统找不到指定的路径。)
        //java.io.FileNotFoundException:  (文件名、目录名或卷标语法不正确。)
        //  System.out.println(MySimHash.readfile("D:\\tests\\orig999.txt\""));
        //java.io.FileNotFoundException:
        System.out.println(MySimHash.readfile(""));
    }

    @Test
    public void testhammingDistance() {
        System.out.println("这是对海明距离的测试");
        System.out.println("相同字符串的海明距离为：" + a.hammingDistance(b));
        System.out.println("用空格分隔的字符串海明距离为：" + a.hammingDistance(c));
        System.out.println("乱序的字符串的海明距离为:" + a.hammingDistance(d));
        // System.out.println(a.hammingDistance(e)); //字符串为null
        // System.out.println(a.hammingDistance(f));   //字符串内容为空
        System.out.println("子串的海明距离为:" + a.hammingDistance(g));
        System.out.println("抄袭版本比原版本长的海明距离:" + a.hammingDistance(h));
    }

    @Test   //测试单个的分词进行hash计算;
    public void testhash() {
        System.out.println("这是对单个的分词进行hash计算");
        System.out.println("活着\t\t" + a.hash("活着"));
        System.out.println("活\t\t" + a.hash("活"));
        System.out.println("代表作\t" + a.hash("代表作"));
        System.out.println("代表\t\t" + a.hash("代表"));
        System.out.println("代\t\t" + a.hash("代"));
    }


    @Test
    public void teststrSimHash() {
        System.out.println("这是对字符串Hash值的测试");
        System.out.println("《活着》是作家余华的代表作之一\t\t\t\t的哈希值:" + a.strSimHash);
        System.out.println("《 活 着 》 是 作 家 余 华 的 代 表 作 之 一\t的哈希值:" + b.strSimHash);
        System.out.println("《余华》的代表作之一是作家活着\t\t\t\t的哈希值:" + c.strSimHash);
        System.out.println("《活着》的代表作\t\t\t\t\t\t\t的哈希值:" + d.strSimHash);
        System.out.println("《活着》是作家余华的代表作之一，这本书讲述了\t\t的哈希值:" + g.strSimHash);

    }

    @Test
    public void testcleanResume() {
        System.out.println("这是对删除字符串里常用符号的测试");
        //删除掉常用符号
        String s1 = "《 活 着 》 是 作 家 余 华 的 代 表 作 之 一";
        System.out.println(new MySimHash(s1, 64).cleanResume(s1));
        String s2 = "《,活,着,》,是,作,家,余,华,的,代,表,作,之,一";
        System.out.println(new MySimHash(s2, 64).cleanResume(s2));
        String s3 = "《?活,着.》。是,作 家!余-华-的.代?表?作!之 一";
        System.out.println(new MySimHash(s3, 64).cleanResume(s3));
    }

    @Test
    public void testmain() throws IOException {
        System.out.println("这是对主函数main的测试");
        //MySimHash.main(args0);//java.lang.Error: 输入的文件路径数少于三个
        MySimHash.main(args00);//java.lang.Error: 输入的文件路径数大于三个
        MySimHash.main(args1);
        MySimHash.main(args2);
        MySimHash.main(args3);
        MySimHash.main(args4);
        MySimHash.main(args5);

    }
}