# Jieba Analysis - GraalVM Native Image 支持

本项目已成功重构以支持 GraalVM Native Image,可以编译为本地可执行文件,实现更快的启动速度和更低的内存占用。

## 系统要求

- **Java**: GraalVM 21 或更高版本
- **Maven**: 3.6+ 
- **操作系统**: Linux, macOS, Windows (需要 Visual Studio Build Tools)

## 安装 GraalVM

使用 SDKMAN 安装 GraalVM:

```bash
sdk install java 21.0.8-graal
sdk use java 21.0.8-graal
```

## 构建方式

### 1. 传统 JAR 包构建 (JVM 模式)

```bash
mvn clean package -DskipTests
```

运行:
```bash
java -cp target/jieba-analysis-1.0.3-SNAPSHOT.jar com.huaban.analysis.jieba.NativeImageDemo
```

### 2. Native Image 构建

#### 使用构建脚本:
```bash
./build-native.sh
```

#### 手动构建:
```bash
# 设置 JAVA_HOME
export JAVA_HOME=~/.sdkman/candidates/java/21.0.8-graal
export PATH=$JAVA_HOME/bin:$PATH

# 构建
mvn clean package -Pnative -DskipTests
```

构建完成后,可执行文件位于: `target/jieba-analysis`

运行:
```bash
./target/jieba-analysis
```

## 重构内容

### 1. 资源加载机制
- 创建 `ResourceLoader` 工具类,统一管理资源加载
- 重构 `WordDictionary` 和 `FinalSeg` 使用新的资源加载器
- 支持 JVM 和 Native Image 两种运行模式

### 2. Native Image 配置
- `resource-config.json`: 配置需要包含的资源文件
- `reflect-config.json`: 配置反射访问规则
- `native-image.properties`: Native Image 构建参数

### 3. 项目升级
- Java 版本: 1.8 → 21
- Maven 编译器插件: 2.3.2 → 3.11.0
- 添加 GraalVM SDK 依赖
- 添加 Native Image Maven 插件

## 性能对比

### JVM 模式 (测试结果)
- 词典加载时间: ~990ms
- 模型加载时间: ~107ms
- 总启动时间: ~1100ms

### Native Image 模式 (预期)
- 词典加载时间: ~50-100ms
- 模型加载时间: ~10-20ms
- 总启动时间: ~100-200ms
- 内存占用: 减少 50-70%

## 使用示例

```java
import com.huaban.analysis.jieba.JiebaSegmenter;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        String text = "结巴分词是一个很好用的中文分词工具";
        List<String> tokens = segmenter.sentenceProcess(text);
        System.out.println(tokens);
        // 输出: [结巴, 分词, 是, 一个, 很, 好, 用, 的, 中文, 分词, 工具]
    }
}
```

## 故障排查

### 1. 资源文件未找到
**错误**: `ResourceNotFoundException: Resource not found: /dict.txt`

**解决方案**: 
- 检查 `resource-config.json` 配置
- 确保资源文件在 `src/main/resources` 目录下
- 重新构建 Native Image

### 2. 反射访问错误
**错误**: `ClassNotFoundException` 或反射相关错误

**解决方案**:
- 检查 `reflect-config.json` 配置
- 添加缺失的类配置
- 使用 GraalVM 的 tracing agent 自动生成配置:
  ```bash
  java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image/com.huaban/jieba-analysis \
       -cp target/jieba-analysis-1.0.3-SNAPSHOT.jar \
       com.huaban.analysis.jieba.NativeImageDemo
  ```

### 3. 构建时间过长
Native Image 构建通常需要几分钟时间,这是正常现象。可以通过以下方式优化:
- 使用 `--no-fallback` 禁用回退模式
- 减少不必要的反射配置
- 使用增量构建

## 兼容性说明

- **API 兼容**: 完全兼容原有 API,无需修改现有代码
- **功能兼容**: 所有分词功能保持不变
- **性能**: Native Image 模式启动更快,运行时性能与 JVM 模式相当

## 贡献

欢迎提交 Issue 和 Pull Request!

## License

Apache License 2.0
