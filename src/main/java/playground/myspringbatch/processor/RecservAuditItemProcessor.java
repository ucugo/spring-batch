package playground.myspringbatch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import playground.myspringbatch.model.OutputItem;

@Component(value ="recservAuditItemProcessor" )
public class RecservAuditItemProcessor implements ItemProcessor<String, OutputItem> {

    @Override
    public OutputItem process(String inputItem) throws Exception {
        OutputItem outputItem = new OutputItem();
        outputItem.setName(inputItem);
        return outputItem;
    }
}
