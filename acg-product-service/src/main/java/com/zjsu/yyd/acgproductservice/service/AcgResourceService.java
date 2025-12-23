package com.zjsu.yyd.acgproductservice.service;

import com.zjsu.yyd.acgproductservice.model.AcgResource;
import com.zjsu.yyd.acgproductservice.repository.AcgResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AcgResourceService {

    @Autowired
    private AcgResourceRepository resourceRepository;

    /**
     * 文件上传根目录
     * 例如：
     * - 本地：D:/acg-data
     * - Docker：/data
     */
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 上传资源文件
     * 数据库存储：resource/xxx.xxx（相对路径）
     */
    public AcgResource uploadResource(MultipartFile file,
                                      Long userId,
                                      String name,
                                      String description,
                                      Long productId) throws IOException {

        // 1️⃣ resource 实际存储目录决定
        Path resourceDir = Paths.get(uploadDir, "resource");
        File dir = resourceDir.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2️⃣ 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + ext;

        // 3️⃣ 实际保存到磁盘（绝对路径）
        Path absolutePath = resourceDir.resolve(filename);
        file.transferTo(absolutePath.toFile());

        // 4️⃣ 数据库存相对路径
        String relativePath = "resource/" + filename;

        AcgResource resource = new AcgResource();
        resource.setUserId(userId);
        resource.setName(name);
        resource.setDescription(description);
        resource.setProductId(productId);
        resource.setResourcePath(relativePath); // ✅ 只存相对路径

        return resourceRepository.save(resource);
    }

    public Optional<AcgResource> getResource(Long id) {
        return resourceRepository.findById(id)
                .filter(resource -> !resource.getIsDeleted());
    }

    /**
     * 根据数据库中的相对路径，获取真实文件
     */
    public File getResourceFile(AcgResource resource) {
        // uploadDir + resource/xxx.xxx
        Path absolutePath = Paths.get(uploadDir, resource.getResourcePath());
        return absolutePath.toFile();
    }

    public List<AcgResource> listByProductId(Long productId) {
        return resourceRepository.findByProductIdAndIsDeletedFalse(productId);
    }

    public boolean deleteResource(Long id) {
        return resourceRepository.findById(id)
                .map(resource -> {
                    resource.setIsDeleted(true);
                    resourceRepository.save(resource);
                    return true;
                })
                .orElse(false);
    }
}
