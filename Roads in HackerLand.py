from collections import defaultdict

n,m = map(int,input().split())
edges = []
parent = [i for i in range (n)]
for i in range (m):
    beg, dest, weight = map(int, input().split())
    edges.append((beg - 1, dest - 1, weight))

#disjoint set implementation

def find(x):
    while x!= parent[parent[x]]:
        parent[x] = parent[parent[x]]
        x = parent[x]
    return x

def union(x,y):
    p1 = find(x)
    p2 = find(y)
    parent[p2] = p1

def connect(x,y):
    p1 = find(x)
    p2 = find(y)
    return p1 == p2

def dfs(src,p = -1):                          # to count edges
    total = 1
    for v,weight in tree[src]:
        if v!=p:
            child = dfs(v,src)
            ans[weight] += (n-child)*child
            total += child
    return total

tree = defaultdict(list)
edges.sort(key = lambda x:x[2])

# build Kruskal's MST
for beg, dest, weight in edges:
    if not connect(beg, dest):              # if in different classes
        union(beg, dest)                    # combine equiv classes
        tree[beg].append((dest, weight))
        tree[dest].append((beg, weight))

ans = [0]*(2*m)
dfs(0)
res = 0
for i in range (len(ans)):
    res += ans[i]*(1 << i)
print(str(bin(res))[2:])