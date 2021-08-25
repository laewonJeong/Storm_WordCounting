---

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/07d4c466-7068-463a-bba3-717a851751a5/Untitled.png)

- $\color{mediumaquamarine}Sentence$ $\color{mediumaquamarine}Spout$
    - {"sentence":"my dog has fleas"}
- $\color{mediumaquamarine}Word$ $\color{mediumaquamarine}Count$ $\color{mediumaquamarine}Bolt$
    - 문장 나누기 볼트가 보낸 Tuple을 받음
    - 단어의 수 집계
    - {"word":"dog","count":5}

---

### $Pom.xml$

- 

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>

        <groupId>org.example</groupId>
        <artifactId>laewon_wc</artifactId>
        <version>1.0-SNAPSHOT</version>
        <dependencies>
            <dependency>
                <groupId>org.apache.storm</groupId>
                <artifactId>storm-client</artifactId>
                <version>2.2.0</version>
            </dependency>
        </dependencies>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.6.1</version>
                    <configuration>
                        <source>1.8</source>
                        <target>1.8</target>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-assembly-plugin</artifactId>
                    <version>3.1.0</version>
                    <configuration>
                        <descriptorRefs>
                            <descriptorRef>jar-with-dependencies</descriptorRef>
                        </descriptorRefs>
                    </configuration>
                    <executions>
                        <execution>
                            <phase>package</phase>
                            <goals>
                                <goal>single</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>

        <properties>
            <maven.compiler.source>16</maven.compiler.source>
            <maven.compiler.target>16</maven.compiler.target>
        </properties>

    </project>
    ```

## $SentenceSpout.java$

- 전체 코드

    ```java
    package lwwc;

    import org.apache.storm.spout.SpoutOutputCollector;
    import org.apache.storm.task.TopologyContext;
    import org.apache.storm.topology.OutputFieldsDeclarer;
    import org.apache.storm.topology.base.BaseRichSpout;
    import org.apache.storm.tuple.Fields;
    import org.apache.storm.tuple.Values;

    import java.util.Map;

    /*
    고정된 수의 문장들을 반복해서 내보내는 데이터 소스를 시뮬레이션 하도록 구현. 각 문장은 단일 필드를가진 튜플로 보내짐
     */

    public class SentenceSpout extends BaseRichSpout {
        private SpoutOutputCollector collector;
        private int index = 0;
        private final String[] sentences ={
               //문장들
        };

        @Override
        public void open(Map<String, Object> map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this.collector = spoutOutputCollector;
        }

        @Override
        public void nextTuple() {
            this.collector.emit(new Values(sentences[index]));
            index++;
            if(index >= sentences.length){
                index = 0;
            }
            try {Thread.sleep(5);}catch (InterruptedException e){}
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("sentence"));
        }
    }
    ```

- 코드 설명

    ```java
    public class SentenceSpout extends **BaseRichSpout** {
        // BaseRichSpout:
        private SpoutOutputCollector collector;
        private int index = 0;
        private final String[] sentences ={
                //문장들
        };
    ```

    - $\color{mediumaquamarine}BaseRichSpout$

        ⇒ IRichSpout 구현을 구현한 편의 클래스

        ⇒ 두 인터페이스의 모든 메소드를 내용이 빈 메소드로 구현해서 필요한 메소드만 구현하면 됨

    - 변수와 문장 정의

    ```java
    @Override
    public void open(**Map**<String, Object> map, **TopologyContext** topologyContext, 
    									**SpoutOutputCollector** spoutOutputCollector) {
            this.collector = spoutOutputCollector;
    }
    ```

    - SpoutOutputCollector 객체의 참조를 인스턴스 변수에 저장하는 일만 하는 간단한 형태
    - 3개의 파리미터를 받음

        ⇒ $\color{mediumaquamarine}Map$: 스톰 설정 정보를 가짐

        ⇒ $\color{mediumaquamarine}TopologyContext$: 토폴로지에 속한 컴포넌트들의 정보

        ⇒ $\color{mediumaquamarine}SpoutOutputCollector$: 튜플을 내보낼 때 사용

    ```java
    		@Override
        public void nextTuple() {
            this.collector.emit(new Values(sentences[index]));
            index++;
            if(index >= sentences.length){
                index = 0;
            }
            try {Thread.sleep(5);}catch (InterruptedException e){}
        }
    ```

    - SpoutOutputCollector를 이용해 튜플을 내보내라고 스파우트에게 요청함
    - 문장 배열의 현재 index값에 해당하는 문장을 내보내고 1 증가시킴

    ```java
    		@Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("sentence"));
        }
    ```

    - $\color{mediumaquamarine}declareOutputFields()$
        - 모든 스톰 컴포넌트(스파우트, 볼트)가 구현해야하는 메소드
        - 컴포넌트가 어떤 스트림을 내보내고 스트림의 튜플이 어떤 필드들로 구성되었는지 스톰에게 알려줌
        - 여기에서는 스파우트가 "sentence"를 가진 튜플로 구성된 한 개의 기본 스트림을 내보냄

## $SplitBolt.java$

- 전체 코드

    ```java
    package lwwc;

    import org.apache.storm.task.OutputCollector;
    import org.apache.storm.task.TopologyContext;
    import org.apache.storm.topology.OutputFieldsDeclarer;
    import org.apache.storm.topology.base.BaseRichBolt;
    import org.apache.storm.tuple.Fields;
    import org.apache.storm.tuple.Tuple;
    import org.apache.storm.tuple.Values;

    import java.util.Map;

    public class SplitBolt extends BaseRichBolt {
        private OutputCollector collector;

        @Override
        public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.collector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            String sentence = tuple.getStringByField("sentence");
            String[] words = sentence.split(" ");
            for (String word: words){
                this.collector.emit(new Values(word));
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }
    }
    ```

- 코드 설명

    ```java
    		@Override
        public void prepare(Map<String, Object> map, TopologyContext topologyContext, 
    												OutputCollector outputCollector) {
            this.collector = outputCollector;
        }
    ```

    - ISpout()의 open()과 비슷
    - 볼트가 초기화 될 때 호출됨 → 데이터베이스 커넥션 같은 자원을 초기화 하기에 적절
    - 인자로 받은 OutputCollector (볼트에서 나가는 친구) 객체의 참조를 저장

    ```java
    		@Override
        public void execute(Tuple tuple) {
            String sentence = tuple.getStringByField("sentence");
            String[] words = sentence.split(" ");
            for (String word: words){
                this.collector.emit(new Values(word));
            }
        }
    ```

    - Bolt의 입력 Tuple 스트림과 새로운 Tuple을 받을 때 마다 호출됨
    - 받은 Tuple에서 'sentence'필드를 문자열로 추출해 문장을 개별 단어로 나눔
    - 각 단어를 새로운 Tuple로 보냄

    ```java
    		@Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }
    ```

    - SplitBolt 클래스가 'word' 필드만 가진 튜플 스트림을 내보냄

## $WordCountBolt.java$

- 전체 코드

    ```java
    package lwwc;

    import org.apache.storm.task.OutputCollector;
    import org.apache.storm.task.TopologyContext;
    import org.apache.storm.topology.OutputFieldsDeclarer;
    import org.apache.storm.topology.base.BaseRichBolt;
    import org.apache.storm.tuple.Fields;
    import org.apache.storm.tuple.Tuple;
    import org.apache.storm.tuple.Values;

    import java.util.HashMap;
    import java.util.Map;

    public class WordCountBolt extends BaseRichBolt {
        private OutputCollector collector;
        private HashMap<String, Long> counter = null;

        @Override
        public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.collector = outputCollector;
            this.counter = new HashMap<String, Long>();
        }

        @Override
        public void execute(Tuple tuple) {
            String word = tuple.getStringByField("word");
            Long count = this.counter.get(word);
            count = count == null?1L:count + 1;
            this.counter.put(word, count);
            this.collector.emit(new Values(word, count));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word", "count"));
        }
    }
    ```

- 코드 설명

    ```java
    		@Override
        public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.collector = outputCollector;
            this.counter = new HashMap<String, Long>();
        }
    ```

    - HashMap<String, Long> 인스턴스 생성
    - 모든 단어와 그 단어의 집계값을 저장하는데 Map을 사용함

    ```java
    		@Override
        public void execute(Tuple tuple) {
            String word = tuple.getStringByField("word");
            Long count = this.counter.get(word);
            count = count == null?1L:count + 1;
            this.counter.put(word, count);
            this.collector.emit(new Values(word, count));
        }
    ```

    - 받은 단어의 count를 맵에서 찾음
        - if 해당 단어가 없음

            → 1으로 초기화

        - else

             → 그 단어의 count를 증가 시키고 증가 시킨 값을 맵에 저장 

    - 단어와 현재 집계값을 포함한 새로운 Tuple을 만들어 내보냄

        → ex) {"word":"dog", "count": 5}

    ```java
    		@Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word", "count"));
        }
    ```

    - word와 그 count값을 포함한 Tuple 스트림을 내보낸다고 정의

## $Report Bolt$

- 전체 코드

    ```java
    package lwwc;

    import org.apache.storm.task.OutputCollector;
    import org.apache.storm.task.TopologyContext;
    import org.apache.storm.topology.OutputFieldsDeclarer;
    import org.apache.storm.topology.base.BaseRichBolt;
    import org.apache.storm.tuple.Tuple;

    import java.util.HashMap;
    import java.util.Map;

    public class ReportBolt extends BaseRichBolt {
        private HashMap<String, Long> counter = null;

        @Override
        public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.counter = new HashMap<String, Long>();
        }

        @Override
        public void execute(Tuple tuple) {
            String word = tuple.getStringByField("word");
            Long count = tuple.getLongByField("count");
            this.counter.put(word, count);
        }

        @Override
        public void cleanup() {
            System.out.println("------- FINAL COUNT -------");
            for (String key:this.counter.keySet()){
                System.out.println(key+": "+this.counter.get(key));
            }
            System.out.println("---------------------------");
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

        }
    }
    ```

- 코드 설명

    ```java
    		@Override
        public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.counter = new HashMap<String, Long>();
        }
    ```

    ```java
    		@Override
        public void execute(Tuple tuple) {
            String word = tuple.getStringByField("word");
            Long count = tuple.getLongByField("count");
            this.counter.put(word, count);
        }
    ```

    - HashMap<String, Long> 객체에 집계값 저장

    ```java
    		@Override
        public void cleanup() {
            System.out.println("------- FINAL COUNT -------");
            for (String key:this.counter.keySet()){
                System.out.println(key+": "+this.counter.get(key));
            }
            System.out.println("---------------------------");
        }
    ```

    - 볼트가 사용한 리소스를 반납하기 위해 사용
    - 볼트가 종료될 때 Storm은 cleanup() 메소드를 호출함

    ```java
    		@Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

        }
    ```

    - 종료 볼트이기 때문에 declareOutputFields() 메소드를 구현하지 않음

        ⇒ 종료 볼트: Tuple을 받기만 하고 더 이상 스트림을 내보내지 않는 

## $WordCountingTopology.java$

- 전체 코드

    ```java
    package lwwc;

    import org.apache.storm.Config;
    import org.apache.storm.StormSubmitter;
    import org.apache.storm.generated.AlreadyAliveException;
    import org.apache.storm.generated.AuthorizationException;
    import org.apache.storm.generated.InvalidTopologyException;
    import org.apache.storm.topology.TopologyBuilder;
    import org.apache.storm.tuple.Fields;

    public class WordCountTopology {
        private static final String SPOUT_ID = "sentence-spout";
        private static final String SPLIT_BOLT_ID = "split-bolt";
        private static final String COUNT_BOLT_ID = "count-bolt";
        private static final String REPORT_BOLT_ID = "report-bolt";
        private static final String TOPOLOGY_NAME = "laewon-word-count-topology";

        public static void main(String[] args) throws InvalidTopologyException, AuthorizationException,
                AlreadyAliveException {
            SentenceSpout spout = new SentenceSpout();
            SplitBolt splitBolt = new SplitBolt();
            WordCountBolt countBolt = new WordCountBolt();
            ReportBolt reportBolt = new ReportBolt();

            TopologyBuilder builder = new TopologyBuilder();
            builder.setSpout(SPOUT_ID, spout);
            builder.setBolt(SPLIT_BOLT_ID, splitBolt).shuffleGrouping(SPOUT_ID);
            builder.setBolt(COUNT_BOLT_ID, countBolt).fieldsGrouping(SPLIT_BOLT_ID, new Fields("word"));
            builder.setBolt(REPORT_BOLT_ID, reportBolt).globalGrouping(COUNT_BOLT_ID);

            Config conf = new Config();
            conf.setDebug(true);
            conf.setNumWorkers(2);

            StormSubmitter.submitTopologyWithProgressBar(
                    TOPOLOGY_NAME,conf, builder.createTopology()
            );

        }
    }
    ```

---

# 실행

## jar파일 생성

- 
    - Maven을 누르고

        ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/d74531b6-6af3-4866-8159-331718d0f794/Untitled.png)

    - clean, compile, package순으로 실행

        ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/0b15025e-0a52-419d-a778-f003d2fc1c3a/Untitled.png)

        ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/48de17ed-8b56-4afe-a9ad-e18d103c3419/Untitled.png)

    - 그러면 생성이 완료됨
    - 생성된 jar파일을 MN으로 가지고옴

## Zookeeper, Nimbus, Supervisor 실행

- 
    - 주키퍼 실행

        ```bash
        ./bin/zkServer.sh start
        ```

        - SN01, SN02, SN03에서 실행

            ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/a3c7e461-473b-48ba-95de-348fe5c01b2c/Untitled.png)

    - Nimbus 실행

        ```bash
        nohup ./bin/storm nimbus > ./logs/nimbus.log 2>&1 &
        nohup ./bin/storm ui > ./logs/ui.log 2>&1 &
        nohup ./bin/storm logviewer > ./logs/logviewer.log 2>&1 &
        ```

        - MN에서 실행
    - Supervisor 실행

        ```bash
        nohup ./bin/storm supervisor > ./logs/supervisor.log 2>&1 &
        nohup ./bin/storm logviewer > ./logs/logviewer.log 2>&1 &
        ```

        - SN01 ~ SN08 실행

## jar파일 실행

- 

    ```bash
    ./bin/storm/jar {패키지 경로}.{프로젝트명칭}.{토폴로지클래스명칭} 토폴로지이름
    ./bin/storm jar /home/lwjeong/storm_jar/laewon_wc-1.0-SNAPSHOT.jar lwwc.WordCountTopology laewon-word-count-topology
    ```

    - MN에서만 실행

        ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/96c3d0e2-aeec-4a7b-82a5-3eb1530ebe3f/Untitled.png)

        - Storm UI 확인 (114.70.235.40:58080)

        ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/30ac2555-f80e-4992-aa7f-e7d7c125dad4/Untitled.png)

        ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/e65ea4a0-8bdd-4b9e-b0c2-956f90ae96bd/Untitled.png)

## 결과 확인

- 
    - kill 해준 후

    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/2fd0b86c-0a61-48df-b723-23e74cf598c7/Untitled.png)

    - 아래 경로의 worker.log를 확인하면 결과가 있음
    - 여기서는 SN03에 report bolt가 있으니 SN03에서 확인하면 됨

        ```bash
        /tools/storm/logs/workers-artifacts/laewon-word-count-topology-5-1629683350/6701$
        여기에 있는 worker.log를 확인하면됨
        vim worker.log
        /FINAL
        ```

        ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/51a7e1e3-47a5-468c-90c8-fb5ac0fd9738/Untitled.png)
