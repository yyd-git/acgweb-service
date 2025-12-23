package com.zjsu.yyd.acgproductservice.service;

import com.zjsu.yyd.acgproductservice.model.AcgProduct;
import com.zjsu.yyd.acgproductservice.model.AcgProductPageDto;
import com.zjsu.yyd.acgproductservice.model.AcgProductType;
import com.zjsu.yyd.acgproductservice.repository.AcgProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * ACG 产品业务层
 * 负责 ACG 产品的新增、查询、分类查询以及软删除等核心业务逻辑
 */
@Service
public class AcgProductService {

    private final AcgProductRepository repository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public AcgProductService(AcgProductRepository repository) {
        this.repository = repository;
    }

    public AcgProduct add(AcgProduct product) {
        return repository.save(product);
    }

    public List<AcgProduct> listAll() {
        return repository.findByIsDeletedFalse();
    }

    public List<AcgProduct> listByType(AcgProductType type) {
        return repository.findByTypeAndIsDeletedFalse(type);
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(product -> {
            product.setIsDeleted(true);
            repository.save(product);
            return true;
        }).orElse(false);
    }

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

    public AcgProductPageDto listByPage(int page, int size, String name, AcgProductType type) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AcgProduct> resultPage = repository.findByNameAndType(
                (name == null || name.isEmpty()) ? null : name,
                type,
                pageable
        );

        AcgProductPageDto dto = new AcgProductPageDto();
        dto.setTotalElements(resultPage.getTotalElements());
        dto.setTotalPages(resultPage.getTotalPages());
        dto.setPage(resultPage.getNumber());
        dto.setSize(resultPage.getSize());
        dto.setContent(resultPage.getContent());

        return dto;
    }

    public String saveCover(MultipartFile file) throws IOException {

        System.out.println("===== saveCover CALLED =====");
        System.out.println("uploadDir = " + uploadDir);
        System.out.println("originalFilename = " + file.getOriginalFilename());

        // ===== 1. 确保 cover 目录存在 =====
        File coverDir = new File(uploadDir, "cover");
        System.out.println("coverDir absolutePath = " + coverDir.getAbsolutePath());
        System.out.println("coverDir exists before mkdirs = " + coverDir.exists());

        if (!coverDir.exists()) {
            boolean created = coverDir.mkdirs();
            System.out.println("mkdirs result = " + created);
        }

        System.out.println("coverDir exists after mkdirs = " + coverDir.exists());
        System.out.println("coverDir isDirectory = " + coverDir.isDirectory());
        System.out.println("coverDir canWrite = " + coverDir.canWrite());

        // ===== 2. 生成文件名 =====
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID() + ext;
        System.out.println("generated fileName = " + fileName);

        // ===== 3. 保存到磁盘 =====
        File dest = new File(coverDir, fileName);
        System.out.println("dest absolutePath = " + dest.getAbsolutePath());

        file.transferTo(dest);

        System.out.println("file saved success");

        // ===== 4. 返回 Web 路径 =====
        return "/cover/" + fileName;
    }

}

