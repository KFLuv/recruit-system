import request from './request'

/**
 * AI 智能助手 API
 */

/**
 * 简历智能分析
 */
export function analyzeResume(candidateId) {
  return request.post('/ai/analyze-resume', { candidateId })
}

/**
 * 生成招聘报表
 */
export function generateReport(reportType, data) {
  return request.post('/ai/report', { reportType, data })
}

/**
 * 生成面试问题
 */
export function generateInterviewQuestions(candidateId, position) {
  return request.post('/ai/interview-questions', { candidateId, position })
}

/**
 * 招聘咨询问答
 */
export function askQuestion(question, context) {
  return request.post('/ai/ask', { question, context })
}

/**
 * 日常聊天
 */
export function chat(message) {
  return request.post('/ai/chat', { message })
}

/**
 * 获取 AI 状态
 */
export function getAiStatus() {
  return request.get('/ai/status')
}
