package com.wangjunyao.middleware.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangjunyao.middleware.server.entity.Fruit;
import com.wangjunyao.middleware.server.entity.Person;
import com.wangjunyao.middleware.server.entity.PhoneUser;
import com.wangjunyao.middleware.server.entity.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisTest2 {

    private static final Logger log = LoggerFactory.getLogger(RedisTest2.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void redisTest1() throws Exception {
        //构造个人实体对象
        Person person = new Person(10013, 23, "阿修罗", "debug", "火星");
        //定义key和value
        final String key = "redis:test:1";
        String value = objectMapper.writeValueAsString(person);
        //写入缓存
        log.info("存入缓存中的用户实体对象信息为：{}", person);
        redisTemplate.opsForValue().set(key, value);
        //从缓存中获取用户实体信息
        Object res = redisTemplate.opsForValue().get(key);
        if (res != null){
            Person resultPerson = objectMapper.readValue(res.toString(), Person.class);
            log.info("从缓存中读取信息：{}", resultPerson);
        }
    }

    /**
     * List - 列表
     * Redis的列表List类型特别适用于 “排名” “排行榜”
     * “近期访问数据列表” 等业务场景
     */
    @Test
    public void redisTest2(){
        List<Person> list = new ArrayList<>();
        list.add(new Person(1, 21, "修罗", "debug", "火星"));
        list.add(new Person(2, 22, "大剩", "jack", "水帘洞"));
        list.add(new Person(3, 23, "盘古", "Lee", "上古"));

        log.info("构造已经排好序的用户对象列表：{}", list);
        //将列表数据存储至Redis的List中
        final String key = "redis:test:2";
        ListOperations listOperations = redisTemplate.opsForList();
        for (Person person : list){
            //往列表中添加数据 - 从队尾中添加
            listOperations.leftPush(key, person);
        }
        //获取Redis中List的数据 - 从队头中遍历获取，直到没有元素为止
        log.info("获取Redis中List的数据 - 从队头中获取");
        Object res = listOperations.rightPop(key);
        Person resPerson;
        while (res != null){
            resPerson = (Person) res;
            log.info("当前数据：{}", resPerson);
            res = listOperations.rightPop(key);
        }
    }

    /**
     * Set - 集合
     * Redis的集合类型可以保证存储数据的唯一、不重复
     * 在实际应用中，Redis的Set类型常用于解决重复提交、剔除重复ID等业务场景
     */
    @Test
    public void redisTest3(){
        List<String> userList = new ArrayList<>();
        userList.add("debug");
        userList.add("jack");
        userList.add("修罗");
        userList.add("大剩");
        userList.add("debug");
        userList.add("jack");
        userList.add("steady heart");
        userList.add("修罗");
        userList.add("大剩");

        log.info("待处理的用户姓名列表：{}", userList);
        //遍历访问，剔除相同姓名的用户并塞入集合中，最终存入缓存中
        final String key = "redis:test:3";
        SetOperations setOperations = redisTemplate.opsForSet();
        for (String user : userList) {
            setOperations.add(key, user);
        }
        //从缓存中获取用户对象集合
        Object res = setOperations.pop(key);
        while (res != null){
            log.info("从缓存中获取的用户集合 - 当前用户：{}", res);
            res = setOperations.pop(key);
        }

    }

    /**
     * ZSet - 有序集合
     * 在实际生产环境中，Redis的有序集合常用于 “排行榜”、“排名”等应用场景
     */
    @Test
    public void redisTest4(){
        List<PhoneUser> list = new ArrayList<>();
        list.add(new PhoneUser("103", 130.0));
        list.add(new PhoneUser("101", 120.0));
        list.add(new PhoneUser("102", 80.0));
        list.add(new PhoneUser("105", 70.0));
        list.add(new PhoneUser("106", 50.0));
        list.add(new PhoneUser("104", 150.0));

        log.info("无序的手机充值列表：{}", list);

        //遍历访问充值对象列表，将信息塞入Redis有序集合中
        final String key = "redis:test:4";

        //因为zSet在add元素进入缓存后，下次就不能进行更新了，为了测试方便
        //进行操作之前先清空该缓存（实际生产环境中不建议这么使用）
        redisTemplate.delete(key);

        //获取有序结合SortedSet操作组件ZSetOperations
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        for(PhoneUser phoneUser : list){
            zSetOperations.add(key, phoneUser, phoneUser.getFare());
        }

        //前端获取访问充值排名靠前的用户列表
        Long size = zSetOperations.size(key);
        //从小到大排序
        Set<PhoneUser> resSet = zSetOperations.range(key, 0, size);
        //从大到小排序
        //Set<PhoneUser> resSet = zSetOperations.reverseRange(key, 0, size)

        //遍历获取有序集合中的元素
        for(PhoneUser phoneUser : resSet){
            log.info("从缓存中读取手机充值记录排序列表，当前记录：{}", phoneUser);
        }

    }

    /**
     * hash
     * 在实际互联网应用，当需要存入缓存中的对象信息具有某种共性时，
     * 为了减少缓存中key的数量，应考虑采用hash存储
     */
    @Test
    public void redisTest5(){
        //构造学生对象列表和水果对象列表
        List<Student> students = new ArrayList<>();
        List<Fruit> fruits = new ArrayList<>();

        //往学生集合中添加学生对象
        students.add(new Student("10010", "debug", "大剩"));
        students.add(new Student("10011", "jack", "修罗"));
        students.add(new Student("10012", "sam", "上古"));

        //往水果集合中添加水果对象
        fruits.add(new Fruit("apple", "红色"));
        fruits.add(new Fruit("orange", "橙色"));
        fruits.add(new Fruit("banana", "黄色"));

        //分别遍历不同的对象列表，并采用Hash存储至缓存中
        final String stuKey = "redis:test:5";
        final String fruKey = "redis:test:6";

        //获取hash存储操作组件HashOperations，遍历获取集合中的对象并添加进缓存中
        HashOperations hashOperations = redisTemplate.opsForHash();

        for (Student student : students){
            hashOperations.put(stuKey, student.getId(), student);
        }

        for (Fruit fruit : fruits){
            hashOperations.put(fruKey, fruit.getName(), fruit);
        }

        //获取学生对象列表与水果对象列表
        Map<String, Student> stuMap = hashOperations.entries(stuKey);
        log.info("获取学生对象列表：{}", stuMap);

        Map<String, Fruit> fruMap = hashOperations.entries(fruKey);
        log.info("获取水果对象列表：{}", fruMap);

        //获取指定的学生对象
        String stuField = "10012";

        Student student = (Student) hashOperations.get(stuKey, stuField);
        log.info("获取指定的学生对象：{} -> {}");

        String fruField = "orange";
        Fruit fruit = (Fruit) hashOperations.get(fruKey, fruField);

        log.info("获取指定的水果对象：{} -> {}", fruit);

    }

    /**
     * 使缓存中的key失效与判断key是否存在，在实际业务场景中是很常用的，最常见的场景包括：
     * （1）将数据库查询到的数据缓存一定的时间TTL，在TTL时间内前端查询
     * 访问数据列表时，只需要在缓存中查询即可，从而减轻数据库的查询压力
     * （2）将数据压入缓存队列中，并设置一定的TTL时间，当TTL时间一到，
     * 将触发监听事件，从而处理相应的业务逻辑
     *
     * key失效与判断是否存在
     * 在调用setex方法中指定key的过期时间
     */
    @Test
    public void redisTest6() throws Exception{
        //构造key与redis操作组件ValueOperations
        final String key1 = "redis:test:6";
        ValueOperations valueOperations = redisTemplate.opsForValue();
        /**
         * 在向缓存中set数据时，提供一个TTL，表示ttl时间一到，
         * 缓存中的key将自动失效，即被清理，在这里TTL是10秒
         */
        valueOperations.set(key1, "expire操作 - 1", 10L, TimeUnit.SECONDS);

        //等待5秒 - 判断key是否还存在
        Thread.sleep(5000);
        Boolean existsKey1 = redisTemplate.hasKey(key1);
        Object value = valueOperations.get(key1);
        log.info("等待5秒 - 判断key是否还存在：{} 对应的值：{}", existsKey1, value);

        //再等待5秒 - 判断key是否还存在
        Thread.sleep(5000);
        existsKey1 = redisTemplate.hasKey(key1);
        value = valueOperations.get(key1);
        log.info("再等待5秒 - 判断key是否还存在：{} 对应的值：{}", existsKey1, value);
    }

    /**
     * 采用RedisTemplate操作组件的Expire()方法指定失效的key
     * @throws Exception
     */
    @Test
    public void redisTest7() throws Exception{
        //构造key与redis操作组件
        final String key2 = "redis:test:7";
        ValueOperations valueOperations = redisTemplate.opsForValue();
        /**
         * 在往缓存中set数据后，采用redisTemplate的expire方法使该key失效
         */
        valueOperations.set(key2, "expire操作 - 2");
        redisTemplate.expire(key2, 10L, TimeUnit.SECONDS);

        //等待5秒 - 判断key是否还存在
        Thread.sleep(5000);
        Boolean existsKey1 = redisTemplate.hasKey(key2);
        Object value = valueOperations.get(key2);
        log.info("等待5秒 - 判断key是否还存在：{} 对应的值：{}", existsKey1, value);

        //再等待5秒 - 判断key是否还存在
        Thread.sleep(5000);
        existsKey1 = redisTemplate.hasKey(key2);
        value = valueOperations.get(key2);
        log.info("再等待5秒 - 判断key是否还存在：{} 对应的值：{}", existsKey1, value);
    }













}
