<template>
  <div :style="bgStyle">
    <div class="page-container">

      <!-- 顶部标题 -->
      <div class="page-header">
        <h1 class="main-title">🎌 ACG 产品展示</h1>
        <p class="subtitle">Anime · Comic · Game · Novel</p>
      </div>

      <!-- 搜索栏 + 添加按钮 -->
      <el-card class="search-card">
        <el-form inline>
          <el-form-item>
            <el-input
              v-model="searchName"
              placeholder="搜索作品名"
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
            <el-button type="primary" @click="loadProducts(0)">
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

      <!-- 卡片列表 -->
      <div class="card-grid">
        <div
          v-for="p in products"
          :key="p.id"
          class="acg-card"
          @click="onCardClick(p)"
        >
          <img class="cover" :src="getCoverUrl(p.coverPath)" />

          <div class="info-layer">
            <h2 class="title">{{ p.name }}</h2>
            <el-tag size="mini" type="success">{{ p.type }}</el-tag>
            <p class="score">⭐ {{ p.totalScore || 0 }}</p>
            <p class="desc">{{ p.description || "暂无简介" }}</p>
          </div>
        </div>
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
        <el-form :model="newProduct" label-width="120px">
          <el-form-item label="产品名称" required>
            <el-input v-model="newProduct.name" />
          </el-form-item>

          <el-form-item label="产品简介">
            <el-input type="textarea" v-model="newProduct.description" />
          </el-form-item>

          <el-form-item label="产品类型" required>
            <el-select v-model="newProduct.type">
              <el-option label="动漫" value="ANIME" />
              <el-option label="漫画" value="COMIC" />
              <el-option label="轻小说" value="NOVEL" />
              <el-option label="游戏" value="GAME" />
            </el-select>
          </el-form-item>

          <el-form-item label="作者 / 开发商">
            <el-input v-model="newProduct.author" />
          </el-form-item>

          <el-form-item label="封面图片">
            <el-upload
              :file-list="coverFileList"
              :on-change="handleCoverChange"
              :auto-upload="false"
              list-type="picture"
              accept="image/*"
            >
              <el-button size="small" type="primary">选择图片</el-button>
            </el-upload>
          </el-form-item>
        </el-form>

        <span slot="footer">
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
        backgroundSize: "cover",
        backgroundPosition: "center",
        minHeight: "100vh",
        padding: "40px 20px",
      },

      products: [],
      currentPage: 0,
      totalPages: 0,
      pageSize: 8,

      searchName: "",
      searchType: "",

      gatewayUrl: "http://localhost:8090",
      defaultCover: "https://via.placeholder.com/300x420?text=No+Cover",

      showAddDialog: false,
      newProduct: {
        name: "",
        description: "",
        type: "",
        author: "",
        coverFile: null,
      },
      coverFileList: [],
    };
  },

  methods: {
    getCoverUrl(path) {
      if (!path) return this.defaultCover;
      return path.startsWith("/")
        ? `${this.gatewayUrl}${path}`
        : `${this.gatewayUrl}/${path}`;
    },

    async loadProducts(page) {
      this.currentPage = page;
      let url = `/acg-product/page?page=${page}&size=${this.pageSize}`;
      if (this.searchType) url += `&type=${this.searchType}`;
      if (this.searchName) url += `&name=${encodeURIComponent(this.searchName)}`;

      const res = await http.get(url);
      if (res.code === 1) {
        this.products = res.data.content || [];
        this.totalPages = res.data.totalPages || 0;
      }
    },

    handlePageChange(page) {
      this.loadProducts(page - 1);
    },

    onCardClick(p) {
      this.$router.push({ name: "product-detail", params: { id: p.id } });
    },

    handleCoverChange(file, fileList) {
      this.newProduct.coverFile = file.raw;
      this.coverFileList = fileList;
    },

    async submitNewProduct() {
      const formData = new FormData();
      Object.keys(this.newProduct).forEach(k => {
        if (this.newProduct[k]) formData.append(k, this.newProduct[k]);
      });

      const res = await http.post("/acg-product", formData);
      if (res.code === 1) {
        this.$message.success("添加成功");
        this.showAddDialog = false;
        this.loadProducts(0);
        this.resetForm();
      }
    },

    resetForm() {
      this.newProduct = {
        name: "",
        description: "",
        type: "",
        author: "",
        coverFile: null,
      };
      this.coverFileList = [];
    },
  },

  mounted() {
    this.loadProducts(0);
  },
};
</script>

<style scoped>
/* 页面整体 */
.page-container {
  max-width: 1100px;
  margin: 0 auto;
}

/* 标题 */
.page-header {
  text-align: center;
  margin-bottom: 30px;
}
.main-title {
  font-size: 3rem;
  color: #ffeaa7;
  text-shadow: 0 0 10px rgba(0,0,0,0.6);
}
.subtitle {
  color: #fff;
  opacity: 0.85;
}

/* 搜索栏 */
.search-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  border-radius: 14px;
  margin-bottom: 30px;
}

/* 卡片网格 */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 30px;
}

/* 卡片主体 */
.acg-card {
  position: relative;
  height: 340px;
  border-radius: 18px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 12px 24px rgba(0,0,0,0.35);
  transition: transform 0.35s ease, box-shadow 0.35s ease;
}

/* 🌸 Hover 效果（核心） */
.acg-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow:
    0 20px 40px rgba(0,0,0,0.45),
    0 0 25px rgba(255, 214, 165, 0.35);
}

/* 封面 */
.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s ease;
}

.acg-card:hover .cover {
  transform: scale(1.06);
}

/* 信息层 */
.info-layer {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to top,
    rgba(0,0,0,0.88),
    rgba(0,0,0,0.25)
  );
  color: #fff;
  padding: 18px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.title {
  font-size: 1.2rem;
  margin-bottom: 6px;
}
.score {
  margin: 6px 0;
  color: #ffd700;
}
.desc {
  font-size: 0.85rem;
  line-height: 1.4;
  opacity: 0.9;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

/* 分页 */
.pagination {
  margin: 40px 0;
  text-align: center;
}
</style>
