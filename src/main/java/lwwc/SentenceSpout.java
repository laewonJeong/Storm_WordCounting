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
    // BaseRichSpout:
    private SpoutOutputCollector collector;
    private int index = 0;
    private final String[] sentences ={
            "The snow glows white on the mountain tonight",
            "Not a footprint to be seen",
            "A kingdom of isolation",
            "And it looks like I'm the queen",
            "The wind is howling like this swirling storm inside",
            "Couldn't keep it in, heaven knows I've tried",
            "Don't let them in, don't let them see",
            "Be the good girl you always have to be",
            "Conceal, don't feel, don't let them know",
            "Well, now they know",
            "Let it go, let it go",
            "Can't hold it back anymore",
            "Let it go, let it go",
            "Turn away and slam the door",
            "I don't care what they're going to say",
            "Let the storm rage on",
            "The cold never bothered me anyway",
            "It's funny how some distance makes everything seem small",
            "And the fears that once controlled me can't get to me at all",
            "It's time to see what I can do",
            "To test the limits and break through",
            "No right, no wrong, no rules for me",
            "I'm free",
            "Let it go, let it go",
            "I am one with the wind and sky",
            "Let it go, let it go",
            "You'll never see me cry",
            "Here I stand and here I stay",
            "Let the storm rage on",
            "My power flurries through the air into the ground",
            "My soul is spiraling in frozen fractals all around",
            "And one thought crystallizes like an icy blast",
            "I'm never going back, the past is in the past",
            "Let it go, let it go",
            "When I'll rise like the break of dawn",
            "Let it go, let it go",
            "That perfect girl is gone",
            "Here I stand in the light of day",
            "Let the storm rage on",
            "The cold never bothered me anyway"
    };

    @Override
    public void open(Map<String, Object> map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
    }

    @Override
    // 문장을 한번씩만 보내도록 수정
    public void nextTuple() {
        this.collector.emit(new Values(sentences[index]));
        index++;
        if(index >= sentences.length){
            try {Thread.sleep(1000110000);}catch (InterruptedException e){}
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("sentence"));
    }
}