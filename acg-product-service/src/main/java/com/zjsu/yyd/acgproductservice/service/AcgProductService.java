package com.zjsu.yyd.acgproductservice.service;

import com.zjsu.yyd.acgproductservice.model.AcgProduct;
import com.zjsu.yyd.acgproductservice.model.AcgProductType;
import com.zjsu.yyd.acgproductservice.repository.AcgProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ACG 产品业务层
 * 负责 ACG 产品的新增、查询、分类查询以及软删除等核心业务逻辑
 */
@Service
public class AcgProductService {

    /**
     * ACG 产品数据访问层
     */
    private final AcgProductRepository repository;

    public AcgProductService(AcgProductRepository repository) {
        this.repository = repository;
    }

    /**
     * 新增 ACG 产品
     *
     * @param product ACG 产品实体
     * @return 保存后的产品信息
     */
    public AcgProduct add(AcgProduct product) {
        return repository.save(product);
    }

    /**
     * 查询所有未被软删除的 ACG 产品
     *
     * @return ACG 产品列表
     */
    public List<AcgProduct> listAll() {
        return repository.findByIsDeletedFalse();
    }

    /**
     * 根据产品类型查询 ACG 产品
     *
     * @param type 产品类型（ANIME / COMIC / NOVEL / GAME）
     * @return 对应类型的 ACG 产品列表
     */
    public List<AcgProduct> listByType(AcgProductType type) {
        return repository.findByTypeAndIsDeletedFalse(type);
    }

    /**
     * 根据 ID 软删除 ACG 产品
     * 仅将 isDeleted 标记为 true，不进行物理删除
     *
     * @param id 产品 ID
     * @return 删除是否成功
     */
    public boolean delete(Long id) {
        return repository.findById(id).map(product -> {
            product.setIsDeleted(true);
            repository.save(product);
            return true;
        }).orElse(false);
    }
    /**
     * 根据 ID 查找 ACG 产品

     */
    public AcgProduct getById(Long id) {
        return repository.findById(id)
                .filter(product -> !product.getIsDeleted())
                .orElse(null);
    }

    public boolean updateScore(Long id, Double score) {
        return repository.findById(id)
                .filter(product -> !product.getIsDeleted())
                .map(product -> {
                    product.setTotalScore(score);
                    repository.save(product);
                    return true;
                })
                .orElse(false);
    }


}
