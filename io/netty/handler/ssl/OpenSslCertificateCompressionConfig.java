package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class OpenSslCertificateCompressionConfig implements Iterable<OpenSslCertificateCompressionConfig.AlgorithmConfig> {
  private final List<AlgorithmConfig> pairList;
  
  private OpenSslCertificateCompressionConfig(AlgorithmConfig... pairs) {
    this.pairList = Collections.unmodifiableList(Arrays.asList(pairs));
  }
  
  public Iterator<AlgorithmConfig> iterator() {
    return this.pairList.iterator();
  }
  
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public static final class Builder {
    private final List<OpenSslCertificateCompressionConfig.AlgorithmConfig> algorithmList = new ArrayList<OpenSslCertificateCompressionConfig.AlgorithmConfig>();
    
    public Builder addAlgorithm(OpenSslCertificateCompressionAlgorithm algorithm, OpenSslCertificateCompressionConfig.AlgorithmMode mode) {
      this.algorithmList.add(new OpenSslCertificateCompressionConfig.AlgorithmConfig(algorithm, mode));
      return this;
    }
    
    public OpenSslCertificateCompressionConfig build() {
      return new OpenSslCertificateCompressionConfig(this.algorithmList.<OpenSslCertificateCompressionConfig.AlgorithmConfig>toArray(new OpenSslCertificateCompressionConfig.AlgorithmConfig[0]));
    }
    
    private Builder() {}
  }
  
  public static final class AlgorithmConfig {
    private final OpenSslCertificateCompressionAlgorithm algorithm;
    
    private final OpenSslCertificateCompressionConfig.AlgorithmMode mode;
    
    private AlgorithmConfig(OpenSslCertificateCompressionAlgorithm algorithm, OpenSslCertificateCompressionConfig.AlgorithmMode mode) {
      this.algorithm = (OpenSslCertificateCompressionAlgorithm)ObjectUtil.checkNotNull(algorithm, "algorithm");
      this.mode = (OpenSslCertificateCompressionConfig.AlgorithmMode)ObjectUtil.checkNotNull(mode, "mode");
    }
    
    public OpenSslCertificateCompressionConfig.AlgorithmMode mode() {
      return this.mode;
    }
    
    public OpenSslCertificateCompressionAlgorithm algorithm() {
      return this.algorithm;
    }
  }
  
  public enum AlgorithmMode {
    Compress, Decompress, Both;
  }
}
