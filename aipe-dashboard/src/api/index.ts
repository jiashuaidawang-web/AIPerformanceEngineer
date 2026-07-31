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

export interface Relationship {
  relationshipId: string
  relationshipType: string
  sourceResourceId: string
  targetResourceId: string
  direction: string
  confidence: number
  status: string
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
  evidenceId?: string
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

export const resourceApi = {
  list: (params?: Record<string, string>) => request.get('/v1/resources', { params }),
  get: (id: string) => request.get(`/v1/resources/${id}`),
  create: (data: Record<string, unknown>) => request.post('/v1/resources', data),
  updateStatus: (id: string, status: string) => request.patch(`/v1/resources/${id}/status`, { status }),
  delete: (id: string) => request.delete(`/v1/resources/${id}`),
}

export const observationApi = {
  query: (params: Record<string, unknown>) => request.get('/v1/observations', { params }),
  trend: (params: Record<string, unknown>) => request.get('/v1/observations/trend', { params }),
  latest: (params: Record<string, unknown>) => request.get('/v1/observations/latest', { params }),
  batch: (data: unknown) => request.post('/v1/observations/batch', data),
}

export const timelineApi = {
  query: (params: Record<string, unknown>) => request.get('/v1/timelines', { params }),
  batch: (params: Record<string, unknown>) => request.get('/v1/timelines/batch', { params }),
  all: (params: Record<string, unknown>) => request.get('/v1/timelines/all', { params }),
  availableMetrics: (resourceId: string, startTime?: number, endTime?: number) =>
    request.get('/v1/timelines/metrics', { params: { resource_id: resourceId, start_time: startTime || 0, end_time: endTime || Date.now() } }),
}

export const relationshipApi = {
  list: (params?: Record<string, string>) => request.get('/v1/relationships', { params }),
  create: (data: Record<string, unknown>) => request.post('/v1/relationships', data),
  delete: (id: string) => request.delete(`/v1/relationships/${id}`),
}

export const topologyApi = {
  current: (resourceId: string, type?: string) =>
    request.get('/v1/topology/current', { params: { resource_id: resourceId, type } }),
}

export const evidenceApi = {
  list: (params?: Record<string, string>) => request.get('/v1/evidences', { params }),
  generate: (data: Record<string, unknown>) => request.post('/v1/evidences/generate', data),
  explain: (id: string) => request.get(`/v1/evidences/${id}/explain`),
  verify: (id: string, approved: boolean) => request.post(`/v1/evidences/${id}/verify`, { approved }),
}

export const knowledgeApi = {
  list: (params?: Record<string, string>) => request.get('/v1/knowledge', { params }),
  create: (data: Record<string, unknown>) => request.post('/v1/knowledge', data),
}

export const recommendationApi = {
  list: (params?: Record<string, string>) => request.get('/v1/recommendations', { params }),
  generate: (data: Record<string, unknown>) => request.post('/v1/recommendations/generate', data),
  approve: (id: string) => request.post(`/v1/recommendations/${id}/approve`),
  reject: (id: string) => request.post(`/v1/recommendations/${id}/reject`),
  execute: (id: string) => request.post(`/v1/recommendations/${id}/execute`),
}

export const executionApi = {
  list: (params?: Record<string, string>) => request.get('/v1/executions', { params }),
  create: (data: Record<string, unknown>) => request.post('/v1/executions', data),
  report: (id: string) => request.get(`/v1/executions/${id}/report`),
}

export const alertApi = {
  listRules: () => request.get('/v1/alerts/rules'),
  createRule: (data: Record<string, unknown>) => request.post('/v1/alerts/rules', data),
  updateRule: (id: string, data: Record<string, unknown>) => request.put(`/v1/alerts/rules/${id}`, data),
  deleteRule: (id: string) => request.delete(`/v1/alerts/rules/${id}`),
  listRecords: (status?: string) => request.get('/v1/alerts/records', { params: status ? { status } : {} }),
  testTrigger: (data: Record<string, unknown>) => request.post('/v1/alerts/test/trigger', data),
}

export const agentApi = {
  list: () => request.get('/v1/agents'),
  delete: (id: string) => request.delete(`/v1/agents/${id}`),
}

export const configApi = {
  publish: (agentId: string, config: Record<string, unknown>) => request.post(`/api/v1/configs/${agentId}/publish`, config),
  sendCommand: (agentId: string, command: string) => request.post(`/api/v1/agents/${agentId}/command`, { command }),
}
