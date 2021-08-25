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