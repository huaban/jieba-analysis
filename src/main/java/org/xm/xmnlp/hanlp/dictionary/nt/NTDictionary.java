package org.xm.xmnlp.hanlp.dictionary.nt;

import org.xm.xmnlp.hanlp.corpus.io.IOUtil;
import org.xm.xmnlp.hanlp.corpus.tag.NT;
import org.xm.xmnlp.hanlp.dictionary.common.CommonDictionary;
import org.xm.xmnlp.hanlp.dictionary.item.EnumItem;
import org.xm.xmnlp.hanlp.utility.ByteUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class NTDictionary extends CommonDictionary<EnumItem<NT>> {
    @Override
    protected EnumItem<NT>[] onLoadValue(String path) {
        EnumItem<NT>[] valueArray = loadDat(path + ".value.dat");
        if (valueArray != null) return valueArray;
        List<EnumItem<NT>> valueList = new LinkedList<>();
        try{
            BufferedReader br = IOUtil.newBufferedReader(path);
            String line;
            while ((line= br.readLine()) !=null){
                Map.Entry<String,Map.Entry<String,Integer>[]> params = EnumItem.create(line);
                EnumItem<NT> NSEnumItem = new EnumItem<>();
                for(Map.Entry<String,Integer> e: params.getValue()){
                    NSEnumItem.labelMap.put(NT.valueOf(e.getKey()),e.getValue());
                }
                valueList.add(NSEnumItem);
            }
            br.close();
        }catch (Exception e){
            logger.warning("读取" + path + "失败" + e);
        }
        valueArray = valueList.toArray(new EnumItem[0]);
        return valueArray;
    }

    @Override
    protected boolean onSaveValue(EnumItem<NT>[] valueArray, String path) {
        return saveDat(path +".value.dat" ,valueArray);
    }
    private EnumItem<NT>[] loadDat(String path)
    {
        byte[] bytes = IOUtil.readBytes(path);
        if (bytes == null) return null;
        NT[] values = NT.values();
        int index = 0;
        int size = ByteUtil.bytesHighFirstToInt(bytes, index);
        index += 4;
        EnumItem<NT>[] valueArray = new EnumItem[size];
        for (int i = 0; i < size; ++i)
        {
            int currentSize = ByteUtil.bytesHighFirstToInt(bytes, index);
            index += 4;
            EnumItem<NT> item = new EnumItem<NT>();
            for (int j = 0; j < currentSize; ++j)
            {
                NT tag = values[ByteUtil.bytesHighFirstToInt(bytes, index)];
                index += 4;
                int frequency = ByteUtil.bytesHighFirstToInt(bytes, index);
                index += 4;
                item.labelMap.put(tag, frequency);
            }
            valueArray[i] = item;
        }
        return valueArray;
    }

    private boolean saveDat(String path, EnumItem<NT>[] valueArray)
    {
        try
        {
            DataOutputStream out = new DataOutputStream(IOUtil.newOutputStream(path));
            out.writeInt(valueArray.length);
            for (EnumItem<NT> item : valueArray)
            {
                out.writeInt(item.labelMap.size());
                for (Map.Entry<NT, Integer> entry : item.labelMap.entrySet())
                {
                    out.writeInt(entry.getKey().ordinal());
                    out.writeInt(entry.getValue());
                }
            }
            out.close();
        }
        catch (Exception e)
        {
            logger.warning("保存失败" + e);
            return false;
        }
        return true;
    }
}
