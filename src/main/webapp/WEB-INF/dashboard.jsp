<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recipe Search Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .analytics-card {
            height: 100%;
            transition: transform 0.2s;
        }
        .analytics-card:hover {
            transform: translateY(-5px);
        }
        .table-responsive {
            max-height: 500px;
            overflow-y: auto;
        }
        .bg-gradient {
            background: linear-gradient(120deg, #84fab0 0%, #8fd3f4 100%);
        }
    </style>
</head>
<body class="bg-light">
    <div class="container-fluid py-4">
        <h1 class="text-center mb-4">Recipe Search Analytics Dashboard</h1>

        <!-- Analytics Cards -->
        <div class="row g-4 mb-4">
            <!-- Average Response Time -->
            <div class="col-md-6 col-lg-3">
                <div class="card analytics-card h-100 border-0 shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title">Average Response Time</h5>
                        <p class="display-6 mb-0">${avgResponseTime} ms</p>
                    </div>
                </div>
            </div>

            <!-- Most Searched Names -->
            <div class="col-md-6 col-lg-3">
                <div class="card analytics-card h-100 border-0 shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title">Top Recipe Searches</h5>
                        <ul class="list-unstyled">
                            <c:forEach items="${mostSearchedNames}" var="item" end="4">
                                <li class="mb-2">
                                    ${item.get("_id")}: ${item.get("count")} searches
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>

            <!-- Most Searched Ingredients -->
            <div class="col-md-6 col-lg-3">
                <div class="card analytics-card h-100 border-0 shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title">Popular Ingredients</h5>
                        <ul class="list-unstyled">
                            <c:forEach items="${mostSearchedIngredients}" var="item" end="4">
                                <li class="mb-2">
                                    ${item.get("_id")}: ${item.get("count")} searches
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>

            <!-- Most Searched Categories -->
            <div class="col-md-6 col-lg-3">
                <div class="card analytics-card h-100 border-0 shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title">Popular Categories</h5>
                        <ul class="list-unstyled">
                            <c:forEach items="${mostSearchedCategories}" var="item" end="4">
                                <li class="mb-2">
                                    ${item.get("_id")}: ${item.get("count")} searches
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Logs Table -->
        <div class="card border-0 shadow-sm">
            <div class="card-header bg-gradient text-white">
                <h5 class="card-title mb-0">Recent Search Logs</h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Timestamp</th>
                                <th>Search Type</th>
                                <th>Search Term</th>
                                <th>Results</th>
                                <th>Response Time</th>
                                <th>Device Info</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${recentLogs}" var="log">
                                <tr>
                                    <td><fmt:formatDate value="${log.get('timestamp')}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                    <td>${log.get('searchType')}</td>
                                    <td>${log.get('searchTerm')}</td>
                                    <td>${log.get('resultCount')}</td>
                                    <td>${log.get('responseTime')} ms</td>
                                    <td>${log.get('deviceInfo')}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 