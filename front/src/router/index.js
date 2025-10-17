import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PostListView from '@/views/PostListView.vue'
import PostDetailView from '@/views/PostDetailView.vue'
import PostCreateView from '@/views/PostCreateView.vue'
import PostEditView from '@/views/PostEditView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/posts',
      name: 'posts',
      component: PostListView,
    },
    {
      path: '/posts/:id',
      name: 'postDetail',
      component: PostDetailView,
    },
    {
      path: '/posts/create',
      name: 'postCreate',
      component: PostCreateView,
    },
    {
      path: '/posts/edit/:id',
      name: 'postEdit',
      component: PostEditView,
    }
  ],
})

export default router
