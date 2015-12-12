package playground.myspringbatch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import playground.myspringbatch.model.InputItem;
import playground.myspringbatch.model.OutputItem;

@Component(value ="recservAuditItemProcessor" )
public class RecservAuditItemProcessor implements ItemProcessor<InputItem, OutputItem> {

    @Override
    public OutputItem process(InputItem inputItem) throws Exception {
        OutputItem outputItem = new OutputItem();
        outputItem.setName(inputItem.getName());
        return outputItem;
    }
}
