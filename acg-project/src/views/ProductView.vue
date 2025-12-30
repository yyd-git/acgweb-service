<template>
  <div :style="bgStyle">
    <div class="page-container">
      <!-- 顶部标题和产品信息 -->
      <el-card class="header-card" shadow="never">
        <h1 class="main-title">{{ product?.name || "加载中..." }}</h1>
        <div class="product-info" v-if="product">
          <p>
            类型：<el-tag>{{ product.type }}</el-tag>
          </p>
          <p>作者/开发商：{{ product.author || "未知" }}</p>
          <p>集数/章节/卷数：{{ product.countDisplay || "暂无" }}</p>
        </div>
      </el-card>

      <!-- 资源列表 -->
      <div class="resource-list">
        <el-card
          v-for="res in resources"
          :key="res.id"
          class="resource-card"
          shadow="hover"
        >
          <div v-if="res.hasResource" class="resource-info">
            <p>资源名称：{{ res.name }}</p>
            <p>描述：{{ res.description || "暂无描述" }}</p>
            <el-button
              type="success"
              size="mini"
              @click="downloadResource(res.resourcePath, res.name)"
            >
              下载
            </el-button>
          </div>
          <div v-else class="no-resource">暂无资源</div>
        </el-card>

        <!-- 上传资源按钮 -->
        <el-button
          type="primary"
          icon="el-icon-upload"
          class="upload-btn"
          @click="openUploadDialog"
        >
          上传资源
        </el-button>
      </div>

      <!-- 上传资源弹窗 -->
      <el-dialog
        title="上传资源"
        :visible.sync="uploadDialogVisible"
        width="500px"
      >
        <el-form :model="uploadForm" label-width="100px">
          <el-form-item label="资源名称" required>
            <el-input v-model="uploadForm.name"></el-input>
          </el-form-item>

          <el-form-item label="资源描述">
            <el-input
              type="textarea"
              v-model="uploadForm.description"
              rows="3"
            ></el-input>
          </el-form-item>

          <el-form-item label="资源文件" required>
            <el-upload
              :action="fakeAction"
              :before-upload="beforeUpload"
              :show-file-list="false"
            >
              <el-button>选择文件</el-button>
            </el-upload>
            <span v-if="uploadForm.file">{{ uploadForm.file.name }}</span>
          </el-form-item>
        </el-form>

        <span slot="footer" class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload">确定上传</el-button>
        </span>
      </el-dialog>

      <!-- 打分区 -->
      <el-card class="rating-card common-card" shadow="hover">
        <p>我的评分：</p>
        <el-rate v-model="userScore" :max="10" show-score></el-rate>
        <div class="btn-container">
          <el-button type="primary" @click="submitScore">提交评分</el-button>
        </div>
      </el-card>

      <!-- 评论区 -->
      <el-card class="comment-card common-card" shadow="hover">
        <el-input
          type="textarea"
          v-model="newComment"
          placeholder="写下你的评论..."
          rows="3"
        ></el-input>
        <div class="btn-container">
          <el-button type="primary" @click="submitComment">发送</el-button>
        </div>
      </el-card>

      <!-- 评论展示 -->
      <div class="comment-list">
        <el-card
          v-for="c in comments"
          :key="c.id"
          class="comment-item"
        >
          <p class="comment-username">{{ c.username }}：</p>
          <p class="comment-content">{{ c.content }}</p>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import bgImage from "@/assets/bg.jpg";
import http from "@/utils/http";
import { getUserIdFromToken } from "@/utils/auth";

export default {
  props: ["id"],
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
      product: null,
      resources: [],
      userScore: 0,
      newComment: "",
      comments: [],
      loading: false,

      uploadDialogVisible: false,
      uploadForm: { file: null, name: "", description: "" },
      fakeAction: "http://localhost/",
      gatewayUrl: "http://localhost:8090",
    };
  },
  mounted() {
    const id = this.$route.params.id;
    if (id) {
      this.loadProductDetail(id);
      this.loadResources(id);
      this.loadComments(id);
    }
  },
  methods: {
    async loadProductDetail(id) {
      this.loading = true;
      try {
        const res = await http.get(`/acg-product/${id}`);
        if (res.code === 1) {
          const p = res.data;
          const typeMap = {
            ANIME: "动漫",
            COMIC: "漫画",
            NOVEL: "轻小说",
            GAME: "游戏",
          };
          let countDisplay = "";
          if (p.episodeCount) countDisplay = `${p.episodeCount}集`;
          else if (p.chapterCount) countDisplay = `${p.chapterCount}章`;
          else if (p.volumeCount) countDisplay = `${p.volumeCount}卷`;

          this.product = {
            name: p.name,
            type: typeMap[p.type] || p.type,
            author: p.author || p.developer || p.studio || "未知",
            countDisplay: countDisplay || "暂无",
            cover: p.coverPath ? `${p.coverPath}` : "",
            totalScore: p.totalScore || 0,
          };
        } else this.$message.error(res.msg || "获取产品信息失败");
      } catch (e) {
        console.error(e);
        this.$message.error("获取产品信息失败");
      } finally {
        this.loading = false;
      }
    },
    async loadResources(id) {
      try {
        const res = await http.get(`/acg-resource/list/${id}`);
        if (res.code === 1) {
          this.resources = res.data.map((r) => ({
            id: r.id,
            hasResource: !!r.resourcePath,
            name: r.name,
            description: r.description,
            resourcePath: r.resourcePath,
          }));
        } else this.$message.error(res.msg || "获取资源失败");
      } catch (e) {
        console.error(e);
        this.$message.error("获取资源失败");
      }
    },
    downloadResource(path, name) {
      if (!path) return;
      const normalizedPath = path.startsWith("/") ? path : `/${path}`;
      const url = `${this.gatewayUrl}${normalizedPath}`;
      const link = document.createElement("a");
      link.href = url;
      link.download = name || "资源文件";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
    openUploadDialog() {
      this.uploadDialogVisible = true;
      this.uploadForm = { file: null, name: "", description: "" };
    },
    beforeUpload(file) {
      this.uploadForm.file = file;
      return false;
    },
    async submitUpload() {
      const userId = getUserIdFromToken();
      if (!userId) {
        this.$message.error("请先登录");
        return;
      }
      if (!this.uploadForm.file || !this.uploadForm.name) {
        this.$message.error("请填写资源名称并选择文件");
        return;
      }

      const formData = new FormData();
      formData.append("file", this.uploadForm.file);
      formData.append("userId", userId);
      formData.append("name", this.uploadForm.name);
      formData.append("description", this.uploadForm.description || "");
      formData.append("productId", this.$route.params.id);

      try {
        const res = await http.post("/acg-resource/upload", formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });

        if (res.code === 1 || res.data?.id) {
          this.$message.success("上传成功");
          this.uploadDialogVisible = false;
          this.uploadForm = { file: null, name: "", description: "" };
          this.loadResources(this.$route.params.id);
        } else this.$message.error(res.msg || "上传失败");
      } catch (e) {
        console.error(e);
        this.$message.error("上传失败");
      }
    },
    async submitScore() {
      const userId = getUserIdFromToken();
      if (!userId) {
        this.$message.error("请先登录");
        return;
      }

      try {
        const res = await http.post("/acg-rating/rate", null, {
          params: {
            userId,
            productId: this.$route.params.id,
            score: this.userScore,
          },
        });

        if (res.code === 1) this.$message.success("评分成功");
        else this.$message.error(res.msg || "评分失败");
      } catch (e) {
        console.error(e);
        this.$message.error("评分失败");
      }
    },
    async loadComments(productId) {
      try {
        const res = await http.get(`/acg-comment/list/${productId}`);
        if (res.code === 1) {
          const commentsWithUser = await Promise.all(
            res.data.map(async (c) => {
              if (c.userId) {
                try {
                  const userRes = await http.get(`/user/${c.userId}`);
                  return {
                    id: c.id,
                    username:
                      userRes.code === 1 && userRes.data
                        ? userRes.data.userName
                        : "未知用户",
                    content: c.content,
                  };
                } catch (e) {
                  console.error(e);
                  return { id: c.id, username: "未知用户", content: c.content };
                }
              } else
                return { id: c.id, username: "未知用户", content: c.content };
            })
          );

          this.comments = commentsWithUser;
        } else this.$message.error(res.msg || "获取评论失败");
      } catch (e) {
        console.error(e);
        this.$message.error("获取评论失败");
      }
    },
    async submitComment() {
      const userId = getUserIdFromToken();
      if (!userId) {
        this.$message.error("请先登录");
        return;
      }
      if (!this.newComment) return;

      try {
        const res = await http.post("/acg-comment/add", null, {
          params: {
            userId,
            productId: this.$route.params.id,
            content: this.newComment,
          },
        });

        if (res.code === 1) {
          this.newComment = "";
          this.$message.success("评论成功");
          this.loadComments(this.$route.params.id);
        } else this.$message.error(res.msg || "评论失败");
      } catch (e) {
        console.error(e);
        this.$message.error("评论失败");
      }
    },
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

.product-info p {
  margin: 5px 0;
  text-align: center;
}

.resource-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.resource-card {
  width: 80%;
  text-align: center;
  padding: 10px;
}

.no-resource {
  color: #999;
  font-style: italic;
}

.upload-btn {
  margin-top: 10px;
  width: 200px;
  align-self: center;
}

/* 打分 & 评论输入卡片 */
.common-card {
  width: 80%;
  margin: 0 auto 20px auto;
  text-align: center;
  border-radius: 18px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.25);
}

.rating-card, .comment-card {
  padding: 20px;
}

.btn-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 评论展示卡片样式（与资源/打分区分开） */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
}

.comment-item {
  width: 80%;
  padding: 15px 20px;
  border-radius: 15px;
  background: #ffffff;
  opacity: 0.95;
  border: 1px solid #ffdd55;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}

.comment-username {
  font-weight: bold;
  color: #ff9900;
  text-align: left;
}

.comment-content {
  color: #333;
  text-align: left;
  margin: 5px 0 0 0;
}
</style>
