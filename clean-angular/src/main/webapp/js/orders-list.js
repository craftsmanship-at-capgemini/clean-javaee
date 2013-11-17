'use strict';


// Declare app level module which depends on filters, and services
angular.module('ordersListApp', ['ngResource', 'ngTouch', 'pasvaz.bindonce'])
    .factory('OrdersData', function($resource) {
        return $resource('data/orders.json', {});
    })
    .controller('OrdersListCtrl', ['$scope', 'OrdersData', function($scope, OrdersData) {
            $scope.orders = {};
            $scope.processedOrder = null;

            OrdersData.query(function(response) {
                $scope.orders = response;
            });

            // Action
            $scope.processOrder = function(order) {
                if ($scope.processedOrder === order) {
                    return;
                } else if (order.state === "done") {
                    return;
                } else if ($scope.processedOrder !== order) {
                    if ($scope.processedOrder !== null) {
                        if (isOrderDone($scope.processedOrder)) {
                            $scope.processedOrder.state = "done";
                            // TODO: POST orderDone method
                        } else if (isOrderStarted($scope.processedOrder)) {
                            $scope.processedOrder.state = "uncompleted";
                        } else {
                            $scope.processedOrder.state = "todo";
                        }
                        postProcessOrder($scope.processedOrder);
                    }
                    order.state = "in-progress";
                    postProcessOrder(order);
                    $scope.processedOrder = order;
                }
            };

            // Action
            $scope.productPrepared = function(order, product) {
                if ($scope.processedOrder !== order) {
                    $scope.processOrder(order);
                } else if (product.done < product.qty) {
                    product.done++;
                    postProcessProduct(order, product);
                }
            };

            // private Rule
            function isOrderStarted(order) {
                for (var i = 0; i < order.products.length; i++) {
                    var product = order.products[i];
                    if (product.done > 0) {
                        return true;
                    }
                }
                return false;
            }

            // private Rule
            function isOrderDone(order) {
                for (var i = 0; i < order.products.length; i++) {
                    var product = order.products[i];
                    if (product.done !== product.qty) {
                        return false;
                    }
                }
                return true;
            }

            // private method
            function postProcessOrder(order) {
                order.showProductsList = order.state === "in-progress" || order.state === "uncompleted";
                for (var i = 0; i < order.products.length; i++) {
                    var product = order.products[i];
                    postProcessProduct(order, product);
                }
            }

            // private method
            function postProcessProduct(order, product) {
                product.isDone = product.done === product.qty;
                product.showProduct = order.state === "in-progress" ||
                    (order.state === "uncompleted" && product.done !== product.qty);
            }

        }]);
