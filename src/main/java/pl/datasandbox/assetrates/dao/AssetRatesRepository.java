package pl.datasandbox.assetrates.dao;

import org.springframework.data.repository.CrudRepository;
import pl.datasandbox.assetrates.model.AssetRate;

import java.util.List;

public interface AssetRatesRepository  extends CrudRepository<AssetRate,Long> {

    List<AssetRate> findAll();
}
