export interface FormField {
  key: string
  label: string
  type: 'text' | 'number' | 'password'
  placeholder?: string
  required?: boolean
}

const CONNECTION_FIELDS: Record<string, FormField[]> = {
  REDIS: [
    { key: 'host', label: 'Host', type: 'text', placeholder: '127.0.0.1', required: true },
    { key: 'port', label: 'Port', type: 'number', placeholder: '6379', required: true },
    { key: 'password', label: 'Password', type: 'password' },
    { key: 'database', label: 'DB Index', type: 'number', placeholder: '0' },
  ],
  MYSQL: [
    { key: 'host', label: 'Host', type: 'text', placeholder: '127.0.0.1', required: true },
    { key: 'port', label: 'Port', type: 'number', placeholder: '3306', required: true },
    { key: 'username', label: 'Username', type: 'text', required: true },
    { key: 'password', label: 'Password', type: 'password' },
    { key: 'database', label: 'Database', type: 'text', required: true },
  ],
  DATABASE: [
    { key: 'host', label: 'Host', type: 'text', placeholder: '127.0.0.1', required: true },
    { key: 'port', label: 'Port', type: 'number', placeholder: '3306', required: true },
    { key: 'username', label: 'Username', type: 'text', required: true },
    { key: 'password', label: 'Password', type: 'password' },
    { key: 'database', label: 'Database', type: 'text', required: true },
  ],
  HOST: [
    { key: 'ip', label: 'IP', type: 'text', placeholder: '10.0.0.1', required: true },
    { key: 'hostname', label: 'Hostname', type: 'text' },
    { key: 'os', label: 'OS', type: 'text', placeholder: 'Linux' },
  ],
  SERVICE: [
    { key: 'endpoint', label: 'Endpoint', type: 'text', placeholder: 'http://localhost:8080' },
    { key: 'port', label: 'Port', type: 'number', placeholder: '8080' },
  ],
  APPLICATION: [
    { key: 'endpoint', label: 'Endpoint', type: 'text', placeholder: 'http://localhost:8080' },
    { key: 'port', label: 'Port', type: 'number', placeholder: '8080' },
  ],
  NGINX: [
    { key: 'host', label: 'Host', type: 'text', required: true },
    { key: 'port', label: 'Port', type: 'number', placeholder: '80', required: true },
    { key: 'stub_status_port', label: 'Stub Status Port', type: 'number', placeholder: '9091' },
  ],
  JVM: [
    { key: 'jmx_port', label: 'JMX Port', type: 'number', placeholder: '9999' },
    { key: 'pid', label: 'PID', type: 'number' },
  ],
}

export const RESOURCE_TYPES = [
  'APPLICATION', 'SERVICE', 'DATABASE', 'MYSQL', 'REDIS',
  'HOST', 'CLUSTER', 'NGINX', 'JVM', 'MIDDLEWARE',
]

export function getFieldsForType(resourceType: string): FormField[] {
  return CONNECTION_FIELDS[resourceType] || []
}

export function defaultMetricForType(resourceType?: string): string {
  const map: Record<string, string> = {
    REDIS: 'redis.memory.used',
    MYSQL: 'mysql.connections.active',
    DATABASE: 'mysql.connections.active',
    HOST: 'cpu.usage',
    JVM: 'jvm.memory.heap.used',
  }
  return map[resourceType || ''] || 'cpu.usage'
}
