'use strict';

describe("OrdersListCtrl", function() {

    var ctrlUnderTest, ordersDataMock;

    beforeEach(module('ordersListApp'));
    beforeEach(inject(function($controller, $rootScope, OrdersData) {
        ctrlUnderTest = $rootScope.$new();
        $controller('OrdersListCtrl', {
            $scope: ctrlUnderTest,
            OrdersData: OrdersData
        });
        ordersDataMock = OrdersData;
        defineOrderMatchers(this);
    }));

    it('should mark first processed order as in-progress', inject(function() {
        // given
        var order = someOrderTodoWithSingleProduct();
        spyOn(ordersDataMock, 'query').andReturn([order]);
        // when
        ctrlUnderTest.processOrder(order);
        // then
        expect(order).toBeProcessedBy(ctrlUnderTest);
    }));

    it('should ignore order switch when next order is same as actual', inject(function() {
        // given
        var order = someOrderTodoWithSingleProduct();
        spyOn(ordersDataMock, 'query').andReturn([order]);
        ctrlUnderTest.processOrder(order);
        // when
        ctrlUnderTest.processOrder(order);
        // then
        expect(order).toBeProcessedBy(ctrlUnderTest);
    }));

    it('should ignore order switch when next order is in done state', inject(function() {
        // given
        var order = someOrderTodoWithSingleProduct();
        var doneOrder = someDoneOrder();
        spyOn(ordersDataMock, 'query').andReturn([order, doneOrder]);
        ctrlUnderTest.processOrder(order);
        // when
        ctrlUnderTest.processOrder(doneOrder);
        // then
        expect(doneOrder).toBeDone();
        expect(order).toBeProcessedBy(ctrlUnderTest);
    }));

    it('should mark order as done when all products are done and next order is marked', inject(function() {
        // given
        var order = someOrderTodoWithSingleProduct();
        var nextOrder = someOrderTodoWithManyProducts();
        spyOn(ordersDataMock, 'query').andReturn([order, nextOrder]);
        ctrlUnderTest.processOrder(order);
        // when
        ctrlUnderTest.productPrepared(order, order.products[0]);
        ctrlUnderTest.processOrder(nextOrder);
        // then
        expect(order).toBeDone();
        expect(nextOrder).toBeProcessedBy(ctrlUnderTest);
    }));

    it('should mark order as uncompleted when NOT all but some products are done and next order is marked', inject(function() {
        // given
        var order = someOrderTodoWithManyProducts();
        var nextOrder = someOrderTodoWithSingleProduct();
        spyOn(ordersDataMock, 'query').andReturn([order, nextOrder]);
        ctrlUnderTest.processOrder(order);
        // when
        ctrlUnderTest.productPrepared(order, order.products[1]);
        ctrlUnderTest.processOrder(nextOrder);
        // then
        expect(order).toBeUncompleted();
        expect(nextOrder).toBeProcessedBy(ctrlUnderTest);
    }));

    it('should mark order as uncompleted when all products are NOT done and next order is marked', inject(function() {
        // given
        var order = someOrderTodoWithManyProducts();
        var nextOrder = someOrderTodoWithSingleProduct();
        spyOn(ordersDataMock, 'query').andReturn([order, nextOrder]);
        ctrlUnderTest.processOrder(order);
        // when
        ctrlUnderTest.productPrepared(order, order.products[1]);
        ctrlUnderTest.processOrder(nextOrder);
        // then
        expect(order).toBeUncompleted();
        expect(nextOrder).toBeProcessedBy(ctrlUnderTest);
    }));

    it('should mark next order as in-progress when next order is in uncompleted state', inject(function() {
        // given
        var order = someOrderTodoWithManyProducts();
        var nextOrder = someUncompletedOrder();
        spyOn(ordersDataMock, 'query').andReturn([order, nextOrder]);
        ctrlUnderTest.processOrder(order);
        // when
        ctrlUnderTest.productPrepared(order, order.products[1]);
        ctrlUnderTest.processOrder(nextOrder);
        // then
        expect(order).toBeUncompleted();
        expect(nextOrder).toBeProcessedBy(ctrlUnderTest);
    }));

    function someOrderTodoWithSingleProduct() {
        return {
            "key": "123456789/A1/2013",
            "state": "todo",
            "products": [
                {"key": "jre-07-666", "done": 0, "qty": 1}
            ]
        };
    }

    function someOrderTodoWithManyProducts() {
        return {
            "key": "987654321/A2/2013",
            "state": "todo",
            "products": [
                {"key": "jre-07-666", "done": 0, "qty": 2},
                {"key": "ang-ul-arjs", "done": 0, "qty": 1}
            ]
        };
    }
    function someUncompletedOrder() {
        return {
            "key": "666654321/A2/2013",
            "state": "uncompleted",
            "products": [
                {"key": "jre-07-666", "done": 0, "qty": 2, "isDone": false, "showProduct": true},
                {"key": "ang-ul-arjs", "done": 1, "qty": 1, "isDone": true, "showProduct": false}
            ],
            "showProductsList": true
        };
    }

    function someDoneOrder() {
        return {
            "key": "564738291/A1/2013",
            "state": "done",
            "products": [
                {"key": "jre-07-666", "done": 1, "qty": 1, "isDone": true, "showProduct": false},
                {"key": "ang-ul-arjs", "done": 1, "qty": 1, "isDone": true, "showProduct": false}
            ],
            "showProductsList": false
        };
    }

    function defineOrderMatchers(test) {
        test.addMatchers({
            toBeProcessedBy: function(controller) {
                this.message = function() {
                    return "Expected " + JSON.stringify(this.actual) + " to processed by given controller\n" +
                        "but controller.processedOrder is " + JSON.stringify(controller.processedOrder);
                };
                if (controller.processedOrder !== this.actual) {
                    return false;
                }
                this.message = function() {
                    return "Expected " + JSON.stringify(this.actual) + " to has state 'in-progress'";
                };
                if (this.actual.state !== 'in-progress') {
                    return false;
                }

                this.message = function() {
                    return "Expected " + JSON.stringify(this.actual) + " to has showProductsList 'true'";
                };
                if (!(this.actual.showProductsList === true)) {
                    return false;
                }

                return true;
            },
            toBeUncompleted: function() {
                this.message = function() {
                    return "Expected " + JSON.stringify(this.actual) + " to has state 'uncompleted'";
                };
                if (this.actual.state !== 'uncompleted') {
                    return false;
                }

                this.message = function() {
                    return "Expected " + JSON.stringify(this.actual) + " to has showProductsList 'true'";
                };
                if (!(this.actual.showProductsList === true)) {
                    return false;
                }

                this.message = function() {
                    return "Expected " + JSON.stringify(this.actual) + " to has NOT all product done";
                };
                var allDone = true;
                for (var i = 0; i < this.actual.products.length; i++) {
                    var product = this.actual.products[i];
                    if (product.isDone === false) {
                        allDone = false;
                    }
                }
                if (allDone) {
                    return false;
                }

                this.message = function() {
                    return "Expected " + JSON.stringify(this.actual) + " to has hidden all done product";
                };
                for (var i = 0; i < this.actual.products.length; i++) {
                    var product = this.actual.products[i];
                    if (product.isDone === true && product.showProduct === true) {
                        return false;
                    }
                    if (product.isDone === false && product.showProduct === false) {
                        return false;
                    }
                }
                return true;
            },
            toBeDone: function() {
                this.message = function() {
                    return "Expected " + JSON.stringify(this.actual) + " to has state 'done'";
                };
                if (this.actual.state !== 'done') {
                    return false;
                }

                this.message = function() {
                    return "Expected " + JSON.stringify(this.actual) + " to has showProductsList 'false' or 'undefined'";
                };
                if (!(this.actual.showProductsList === false || this.actual.showProductsList === undefined)) {
                    return false;
                }

                this.message = function() {
                    return "Expected " + JSON.stringify(this.actual) + " to has all product done and hidden";
                };
                for (var i = 0; i < this.actual.products.length; i++) {
                    var product = this.actual.products[i];
                    if (product.isDone === false) {
                        return false;
                    }
                    if (product.showProduct === true) {
                        return false;
                    }
                }
                return true;
            }
        });
    }
});
