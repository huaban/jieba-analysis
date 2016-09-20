package org.xm.xmnlp.hanlp.dictionary.nr;

import org.xm.xmnlp.hanlp.corpus.io.IOUtil;
import org.xm.xmnlp.hanlp.corpus.tag.NR;
import org.xm.xmnlp.hanlp.dictionary.common.CommonDictionary;
import org.xm.xmnlp.hanlp.dictionary.item.EnumItem;
import org.xm.xmnlp.hanlp.utility.ByteUtil;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class NRDictionary extends CommonDictionary<EnumItem<NR>> {
    @Override
    protected EnumItem<NR>[] onLoadValue(String path) {
        EnumItem<NR>[] valueArray = loadDat(path + ".value.dat");
        if (valueArray != null) {
            return valueArray;
        }
        List<EnumItem<NR>> valueList = new LinkedList<EnumItem<NR>>();
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            while ((line = br.readLine()) != null) {
                Map.Entry<String, Map.Entry<String, Integer>[]> args = EnumItem.create(line);
                EnumItem<NR> nrEnumItem = new EnumItem<NR>();
                for (Map.Entry<String, Integer> e : args.getValue()) {
                    nrEnumItem.labelMap.put(NR.valueOf(e.getKey()), e.getValue());
                }
                valueList.add(nrEnumItem);
            }
            br.close();
        } catch (Exception e) {
            logger.severe("读取" + path + "失败[" + e + "]\n该词典这一行格式不对：" + line);
            return null;
        }
        valueArray = valueList.toArray(new EnumItem[0]);
        return valueArray;
    }

    @Override
    protected boolean onSaveValue(EnumItem<NR>[] valueArray, String path) {
        return saveDat(path + ".value.dat", valueArray);
    }

    private EnumItem<NR>[] loadDat(String path) {
        byte[] bytes = IOUtil.readBytes(path);
        if (bytes == null) return null;
        NR[] nrArray = NR.values();
        int index = 0;
        int size = ByteUtil.bytesHighFirstToInt(bytes, index);
        index += 4;
        EnumItem<NR>[] valueArray = new EnumItem[size];
        for (int i = 0; i < size; ++i) {
            int currentSize = ByteUtil.bytesHighFirstToInt(bytes, index);
            index += 4;
            EnumItem<NR> item = new EnumItem<NR>();
            for (int j = 0; j < currentSize; ++j) {
                NR nr = nrArray[ByteUtil.bytesHighFirstToInt(bytes, index)];
                index += 4;
                int frequency = ByteUtil.bytesHighFirstToInt(bytes, index);
                index += 4;
                item.labelMap.put(nr, frequency);
            }
            valueArray[i] = item;
        }
        return valueArray;
    }

    private boolean saveDat(String path, EnumItem<NR>[] valueArray) {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(path));
            out.writeInt(valueArray.length);
            for (EnumItem<NR> item : valueArray) {
                out.writeInt(item.labelMap.size());
                for (Map.Entry<NR, Integer> entry : item.labelMap.entrySet()) {
                    out.writeInt(entry.getKey().ordinal());
                    out.writeInt(entry.getValue());
                }
            }
            out.close();
        } catch (Exception e) {
            logger.warning("保存失败" + e);
            return false;
        }
        return true;
    }
}
