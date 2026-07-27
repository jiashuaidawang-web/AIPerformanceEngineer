import request from '@/utils/request'

export interface Resource {
  resourceId: string
  resourceName: string
  resourceType: string
  resourceCategory: string
  status: string
  businessSystem: string
  cluster: string
  environment: string
  version: number
  labels: Record<string, string>
  attributes: Record<string, string>
  createdTime: string
  updatedTime: string
}

export interface Observation {
  observationId: string
  resourceId: string
  type: string
  name: string
  value: number
  unit: string
  timestamp: number
}

export interface TimelineStats {
  min: number
  max: number
  avg: number
  stdDev: number
  count: number
}

export interface TimelineData {
  timelineId: string
  resourceId: string
  metricName: string
  startTime: number
  endTime: number
  pointCount: number
  points: Array<{ timestamp: number; value: number; unit: string }>
  stats: TimelineStats
}

export interface Evidence {
  evidenceId: string
  evidenceType: string
  title: string
  description: string
  rootResourceId: string
  confidence: number
  status: string
  reasoningSteps: Array<{ step: number; action: string; result: string; confidence: number }>
}

export interface Knowledge {
  knowledgeId: string
  title: string
  description: string
  knowledgeType: string
  confidence: number
  successRate: number
  applicableConditions: Record<string, string>
}

export interface Recommendation {
  recommendationId: string
  knowledgeId: string
  targetResourceId: string
  title: string
  priority: string
  confidence: number
  status: string
  executionPlan: string[]
  rollbackPlan: string[]
}

export interface Execution {
  executionId: string
  recommendationId: string
  executor: string
  executionType: string
  status: string
  improvementScore: number
  startedAt: string
  finishedAt: string
}

// Resource APIs
export const resourceApi = {
  list: (params?: any) => request.get('/v1/resources', { params }),
  get: (id: string) => request.get(`/v1/resources/${id}`),
  create: (data: Partial<Resource>) => request.post('/v1/resources', data),
  delete: (id: string) => request.delete(`/v1/resources/${id}`),
}

// Observation APIs
export const observationApi = {
  query: (params: any) => request.get('/v1/observations', { params }),
  batch: (data: any) => request.post('/v1/observations/batch', data),
}

// Timeline APIs
export const timelineApi = {
  query: (params: any) => request.get('/v1/timelines', { params }),
}

// Evidence APIs
export const evidenceApi = {
  list: (params?: any) => request.get('/v1/evidences', { params }),
  generate: (data: any) => request.post('/v1/evidences/generate', data),
  explain: (id: string) => request.get(`/v1/evidences/${id}/explain`),
}

// Knowledge APIs
export const knowledgeApi = {
  list: (params?: any) => request.get('/v1/knowledge', { params }),
  create: (data: any) => request.post('/v1/knowledge', data),
}

// Recommendation APIs
export const recommendationApi = {
  list: (params?: any) => request.get('/v1/recommendations', { params }),
  generate: (data: any) => request.post('/v1/recommendations/generate', data),
  approve: (id: string) => request.post(`/v1/recommendations/${id}/approve`),
}

// Execution APIs
export const executionApi = {
  list: (params?: any) => request.get('/v1/executions', { params }),
  create: (data: any) => request.post('/v1/executions', data),
  report: (id: string) => request.get(`/v1/executions/${id}/report`),
}
