<template>
  <div :style="bgStyle">
    <div class="page-container">
      <!-- 顶部标题 -->
      <el-card class="header-card" shadow="never">
        <h1 class="main-title">🎌 ACG 产品展示</h1>
        <p class="subtitle">发现你感兴趣的动漫 / 漫画 / 游戏 / 轻小说</p>
      </el-card>

      <!-- 搜索栏 + 添加按钮 -->
      <el-card class="search-card">
        <el-form inline>
          <el-form-item>
            <el-input
              v-model="searchName"
              placeholder="搜索产品名称"
              clearable
              prefix-icon="el-icon-search"
            />
          </el-form-item>

          <el-form-item>
            <el-select v-model="searchType" placeholder="全部分类" clearable>
              <el-option label="动漫" value="ANIME" />
              <el-option label="漫画" value="COMIC" />
              <el-option label="轻小说" value="NOVEL" />
              <el-option label="游戏" value="GAME" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="loadProducts(0)">
              搜索
            </el-button>
          </el-form-item>

          <el-form-item>
            <el-button type="success" icon="el-icon-plus" @click="showAddDialog = true">
              添加产品
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 产品列表 -->
      <div class="card-list">
        <el-card
          v-for="p in products"
          :key="p.id"
          class="product-card"
          shadow="hover"
        >
          <!-- 点击覆盖层 -->
          <div class="card-click-overlay" @click="onCardClick(p)"></div>

          <div class="card-body">
            <!-- 使用 getCoverUrl 方法 -->
            <img
              :src="getCoverUrl(p.coverPath)"
              class="cover"
            />
            <div class="info">
              <div class="title-row">
                <h2>{{ p.name }}</h2>
                <el-tag size="mini" type="success">{{ p.type }}</el-tag>
              </div>
              <p class="score">⭐ 评分：{{ p.totalScore || 0 }}</p>
              <p class="desc">{{ p.description || "暂无简介" }}</p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :current-page="currentPage + 1"
          :page-size="pageSize"
          :total="totalPages * pageSize"
          @current-change="handlePageChange"
        />
      </div>

      <!-- 添加产品弹窗 -->
      <el-dialog
        title="添加 ACG 产品"
        :visible.sync="showAddDialog"
        width="600px"
        @close="resetForm"
      >
        <el-form :model="newProduct" ref="addForm" label-width="120px">
          <el-form-item label="产品名称" required>
            <el-input v-model="newProduct.name" />
          </el-form-item>

          <el-form-item label="产品简介">
            <el-input type="textarea" v-model="newProduct.description" />
          </el-form-item>

          <el-form-item label="产品类型" required>
            <el-select v-model="newProduct.type" placeholder="请选择类型">
              <el-option label="动漫" value="ANIME" />
              <el-option label="漫画" value="COMIC" />
              <el-option label="轻小说" value="NOVEL" />
              <el-option label="游戏" value="GAME" />
            </el-select>
          </el-form-item>

          <el-form-item label="作者 / 开发商">
            <el-input v-model="newProduct.author" placeholder="作者/开发商" />
          </el-form-item>

          <el-form-item label="集数 / 章节 / 卷数">
            <el-select
              v-model="countType"
              placeholder="选择类型"
              style="width: 120px; margin-right: 10px"
            >
              <el-option label="集数" value="episodeCount" />
              <el-option label="章节" value="chapterCount" />
              <el-option label="卷数" value="volumeCount" />
            </el-select>
            <el-input-number
              v-model="newProduct[countType]"
              placeholder="数量"
              style="width: 120px"
            />
          </el-form-item>

          <el-form-item label="封面图片">
            <el-upload
              :file-list="coverFileList"
              :on-change="handleCoverChange"
              :before-upload="beforeUpload"
              :auto-upload="false"
              accept="image/*"
              list-type="picture"
            >
              <el-button size="small" type="primary">选择图片</el-button>
            </el-upload>
          </el-form-item>
        </el-form>

        <span slot="footer" class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="submitNewProduct">提交</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import bgImage from "@/assets/bg.jpg";
import http from "@/utils/http";

export default {
  data() {
    return {
      bgStyle: {
        backgroundImage: `url(${bgImage})`,
        backgroundRepeat: "no-repeat",
        backgroundPosition: "center center",
        backgroundSize: "cover",
        minHeight: "100vh",
        padding: "40px 20px",
      },
      products: [],
      currentPage: 0,
      totalPages: 0,
      pageSize: 6,
      searchName: "",
      searchType: "",
      defaultCover: "https://via.placeholder.com/150x220?text=No+Cover",
      gatewayUrl: "http://localhost:8090",
      showAddDialog: false,
      countType: "episodeCount",
      newProduct: {
        name: "",
        description: "",
        type: "",
        author: "",
        episodeCount: null,
        chapterCount: null,
        volumeCount: null,
        coverFile: null,
      },
      coverFileList: [],
    };
  },
  methods: {
    // 新增封面 URL 拼接方法
    getCoverUrl(path) {
      if (!path) return this.defaultCover;
      return path.startsWith("/") ? `${this.gatewayUrl}${path}` : `${this.gatewayUrl}/${path}`;
    },

    async loadProducts(page) {
      this.currentPage = page;
      let url = `/acg-product/page?page=${page}&size=${this.pageSize}`;
      if (this.searchType) url += `&type=${this.searchType}`;
      if (this.searchName) url += `&name=${encodeURIComponent(this.searchName)}`;

      try {
        const res = await http.get(url);
        if (res.code !== 1) {
          this.$message.error(res.msg || "加载失败");
          return;
        }
        this.products = res.data.content || [];
        this.totalPages = res.data.totalPages || 0;
      } catch (e) {
        this.$message.error("加载失败");
      }
    },

    handlePageChange(page) {
      this.loadProducts(page - 1);
    },

    onCardClick(p) {
      if (!p.id) {
        this.$message.warning("产品 ID 为空，无法跳转");
        return;
      }
      this.$router
        .push({ name: "product-detail", params: { id: p.id } })
        .catch(() => this.$message.error("跳转失败"));

      this.$message.info(`点击了产品：${p.name || "未知"}`);
    },

    handleCoverChange(file, fileList) {
      this.newProduct.coverFile = file.raw;
      this.coverFileList = fileList;
    },

    beforeUpload() {
      return false;
    },

    async submitNewProduct() {
      if (!this.newProduct.name) {
        this.$message.warning("请填写产品名称");
        return;
      }
      if (!this.newProduct.type) {
        this.$message.warning("请选择产品类型");
        return;
      }

      const formData = new FormData();
      formData.append("name", this.newProduct.name);
      formData.append("type", this.newProduct.type);
      if (this.newProduct.description) formData.append("description", this.newProduct.description);
      if (this.newProduct.author) formData.append("author", this.newProduct.author);
      if (this.countType === "episodeCount" && this.newProduct.episodeCount != null) formData.append("episodeCount", this.newProduct.episodeCount);
      if (this.countType === "chapterCount" && this.newProduct.chapterCount != null) formData.append("chapterCount", this.newProduct.chapterCount);
      if (this.countType === "volumeCount" && this.newProduct.volumeCount != null) formData.append("volumeCount", this.newProduct.volumeCount);
      if (this.newProduct.coverFile) formData.append("coverFile", this.newProduct.coverFile);

      try {
        const res = await http.post("/acg-product", formData);
        if (res.code === 1) {
          this.$message.success("添加成功");
          this.showAddDialog = false;
          this.loadProducts(0);
          this.resetForm();
        } else {
          this.$message.error(res.msg || "添加失败");
        }
      } catch (e) {
        this.$message.error("添加失败");
      }
    },

    resetForm() {
      this.newProduct = {
        name: "",
        description: "",
        type: "",
        author: "",
        episodeCount: null,
        chapterCount: null,
        volumeCount: null,
        coverFile: null,
      };
      this.countType = "episodeCount";
      this.coverFileList = [];
    },
  },
  mounted() {
    this.loadProducts(0);
  },
};
</script>

<style scoped>
.page-container {
  max-width: 900px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.25);
  padding: 30px;
  border-radius: 15px;
  backdrop-filter: blur(15px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.header-card {
  text-align: center;
  margin-bottom: 20px;
  background: transparent;
  box-shadow: none;
}
.main-title {
  font-size: 3rem;
  font-weight: bold;
  color: #ffdd55;
  text-shadow: 1px 1px 2px #000;
}
.subtitle {
  color: #fff;
}

.search-card {
  margin-bottom: 30px;
  background: rgba(255, 255, 255, 0.15);
  box-shadow: none;
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.product-card {
  position: relative;
  cursor: pointer;
}
.card-click-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 10;
}
.card-body {
  display: flex;
}
.cover {
  width: 150px;
  height: 220px;
  object-fit: cover;
  border-radius: 6px;
}
.info {
  flex: 1;
  padding-left: 20px;
  display: flex;
  flex-direction: column;
}
.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.title-row h2 {
  margin: 0;
  color: #333;
}
.score {
  margin: 10px 0;
  color: #ffd055;
  font-weight: bold;
}
.desc {
  color: #000;
  line-height: 1.6;
}

.pagination {
  margin-top: 30px;
  text-align: center;
}
</style>
