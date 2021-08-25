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