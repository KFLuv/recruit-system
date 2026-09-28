import request from './request'

// 工作台统计：使用后端 /recruit-plans/stats 接口
export function getDashboardStats() {
  return Promise.all([
    request.get('/recruit-plans/stats'),
    request.get('/candidates')
  ]).then(([statsRes, candidatesRes]) => {
    const a = statsRes.data || {}
    return {
      success: true,
      data: {
        planCount: a.totalPlans || 0,
        approvedCount: a.approvedCount || 0,
        draftCount: a.draftCount || 0,
        submittingCount: a.submittingCount || 0,
        rejectedCount: a.rejectedCount || 0,
        archivedCount: a.archivedCount || 0,
        totalBudget: a.totalBudget || 0,
        totalHeadcount: a.totalHeadcount || 0,
        candidateCount: (candidatesRes.data || []).length
      }
    }
  })
}

// 近期动态：从招聘计划获取最近变更记录（最近 8 条）
export function getRecentActivities() {
  return request.get('/recruit-plans').then(res => {
    const statusText = {
      draft: '创建草稿',
      submitting: '提交审批',
      approved: '已审批通过',
      rejected: '被退回',
      archived: '已归档'
    }
    return {
      success: true,
      data: (res.data || [])
        .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
        .slice(0, 8)
        .map(p => ({
          id: p.id,
          content: `${p.planName} — ${statusText[p.status] || p.status}`,
          createTime: p.createdAt || '',
          type: p.status === 'approved' ? '审批' : '计划'
        }))
    }
  })
}
