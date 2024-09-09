package io.github.elcolto.geokjson.turf.calculation.concavehull

import io.github.elcolto.geokjson.geojson.Point
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * ConcaveHull.kt - 5/11/20
 *
 * @author Daniil Pozdnyakov https://github.com/makaronis
 * @version 1.0
 *
 * This is an implementation of the algorithm described by Adriano Moreira and Maribel Yasmina Santos:
 * CONCAVE HULL: A K-NEAREST NEIGHBOURS APPROACH FOR THE COMPUTATION OF THE REGION OCCUPIED BY A SET OF POINTS.
 * GRAPP 2007 - International Conference on Computer Graphics Theory and Applications; pp 61-68.
 *
 * https://repositorium.sdum.uminho.pt/bitstream/1822/6429/1/ConcaveHull_ACM_MYS.pdf
 *
 * Based on https://github.com/Merowech/java-concave-hull/blob/master/ConcaveHull.java
 */
internal object ConcaveHull {

    private fun euclideanDistance(a: Point, b: Point): Double {
        return sqrt((a.x - b.x).pow(2.0) + (a.y - b.y).pow(2.0))
    }

    private fun kNearestNeighbors(
        points: MutableList<Point>,
        centerPoint: Point,
        k: Int
    ): MutableList<Pair<Double, Point>> {
        val nearestList =   points.map { point ->
            euclideanDistance(centerPoint, point) to point
        }.sortedBy { it.first }


        val result = ArrayList<Pair<Double, Point>>()
        for (i in 0 until k.coerceAtMost(nearestList.size)) {
            val neighbour = nearestList.getOrNull(i) ?: continue
            result.add(neighbour)
        }
        return result
    }

    private fun findMinYPoint(points: MutableList<Point>): Point {
        points.sortWith { o1, o2 -> o1.y.compareTo(o2.y) }
        return points[0]
    }

    private fun calculateAngle(o1: Point?, o2: Point?): Double = atan2(o2!!.y - o1!!.y, o2.x - o1.x)

    private fun angleDifference(a1: Double, a2: Double): Double {
        // calculate angle difference in clockwise directions as radians
        return when {
            a1 > 0 && a2 >= 0 && a1 > a2 -> abs(a1 - a2)
            a1 >= 0 && a2 > 0 && a1 < a2 -> 2 * PI + a1 - a2
            a1 < 0 && a2 <= 0 && a1 < a2 -> 2 * PI + a1 + abs(a2)
            a1 <= 0 && a2 < 0 && a1 > a2 -> abs(a1 - a2)
            a1 <= 0 && 0 < a2 -> 2 * PI + a1 - a2
            a1 >= 0 && 0 >= a2 -> a1 + abs(a2)
            else -> 0.0
        }
    }

    private fun sortByAngle(
        nearestPoints: MutableList<Pair<Double, Point>>,
        currentPoint: Point,
        prevAngle: Double
    ): List<Point> {
        // Sort by angle descending
        val sortedList = nearestPoints.asReversed()
        sortedList.sortWith { o1, o2 ->
            val a1 = angleDifference(prevAngle, calculateAngle(currentPoint, o1.second))
            val a2 = angleDifference(prevAngle, calculateAngle(currentPoint, o2.second))
            val compare = a2.compareTo(a1)
            // if two angle a same, select one that more closely
            if (compare == 0) {
                val firstDistance = o1.first
                val secondDistance = o2.first
                firstDistance.compareTo(secondDistance)
            } else {
                compare
            }
        }
        return sortedList.map { it.second }
    }

    private fun intersect(l1p1: Point?, l1p2: Point?, l2p1: Point?, l2p2: Point?): Boolean {
        // calculate part equations for line-line intersection
        val a1 = l1p2!!.y - l1p1!!.y
        val b1 = l1p1.x - l1p2.x
        val c1 = a1 * l1p1.x + b1 * l1p1.y
        val a2 = l2p2!!.y - l2p1!!.y
        val b2 = l2p1.x - l2p2.x
        val c2 = a2 * l2p1.x + b2 * l2p1.y
        // calculate the divisor
        val tmp = a1 * b2 - a2 * b1

        // calculate intersection point x coordinate
        val pX = (c1 * b2 - c2 * b1) / tmp
        if (pX.isNaN()) return false

        // check if intersection x coordinate lies in line line segment
        if (pX > l1p1.x && pX > l1p2.x || pX > l2p1.x && pX > l2p2.x || pX < l1p1.x && pX < l1p2.x || pX < l2p1.x && pX < l2p2.x) {
            return false
        }

        // calculate intersection point y coordinate
        val pY = (a1 * c2 - a2 * c1) / tmp
        if (pY.isNaN()) return false

        // check if intersection y coordinate lies in line line segment
        return !(pY > l1p1.y && pY > l1p2.y || pY > l2p1.y && pY > l2p2.y || pY < l1p1.y && pY < l1p2.y || pY < l2p1.y && pY < l2p2.y)
    }

    private fun pointInPolygon(p: Point?, pp: ArrayList<Point>): Boolean {
        var result = false
        var i = 0
        var j = pp.size - 1
        while (i < pp.size) {
            if (pp[i].y > p!!.y != pp[j].y > p.y && p.x < (pp[j].x - pp[i].x) * (p.y - pp[i].y) / (pp[j].y - pp[i].y) + pp[i].x) {
                result = !result
            }
            j = i++
        }
        return result
    }

    fun calculateConcaveHull(
        pointArrayList: List<Point>, k: Int
    ): List<Point> {

        // the resulting concave hull
        val concaveHull = ArrayList<Point>()

        // optional remove duplicates
        val pointArraySet = pointArrayList.distinct().toMutableList()

        // k has to be greater than 3 to execute the algorithm
        var kk = k.coerceAtLeast(3)

        // return Points if already Concave Hull
        if (pointArraySet.size < 3) {
            return pointArraySet
        }

        // make sure that k neighbors can be found
        kk = kk.coerceAtMost(pointArraySet.size - 1)

        // find first point and remove from point list
        val firstPoint = findMinYPoint(pointArraySet)
        concaveHull.add(firstPoint)
        var currentPoint = firstPoint
        pointArraySet.remove(firstPoint)

        var previousAngle = 0.0
        var step = 2
        while ((currentPoint !== firstPoint || step == 2) && pointArraySet.size > 0) {
            // after 3 steps add first point to dataset, otherwise hull cannot be closed
            if (step == 5) {
                pointArraySet.add(firstPoint)
            }

            // get k nearest neighbors of current point
            val kNearestPoints = kNearestNeighbors(pointArraySet, currentPoint, kk)

            // sort points by angle clockwise
            val clockwisePoints = sortByAngle(kNearestPoints, currentPoint, previousAngle)
            // check if clockwise angle nearest neighbors are candidates for concave hull
            var its = true
            var i = -1
            while (its && i < clockwisePoints.size - 1) {
                i++
                var lastPoint = 0
                if (clockwisePoints[i] === firstPoint) {
                    lastPoint = 1
                }

                // check if possible new concave hull point intersects with others
                var j = 2
                its = false
                while (!its && j < concaveHull.size - lastPoint) {
                    its = intersect(
                        currentPoint, clockwisePoints[i], concaveHull[step - 2 - j], concaveHull[step - 1 - j]
                    )
                    j++
                }
            }

            // if there is no candidate increase k - try again
            if (its) {
                return calculateConcaveHull(pointArrayList, k + 1);
            }

            // add candidate to concave hull and remove from dataset
            currentPoint = clockwisePoints[i]
            concaveHull.add(currentPoint)
            pointArraySet.remove(currentPoint)

            // calculate last angle of the concave hull line
            previousAngle = calculateAngle(concaveHull[step - 1], concaveHull[step - 2])
            step++
        }

        // Check if all points are contained in the concave hull
        var insideCheck = true
        var i = pointArraySet.size - 1
        while (insideCheck && i > 0) {
            insideCheck = pointInPolygon(pointArraySet[i], concaveHull)
            i--
        }

        // if not all points inside -  try again
        return if (!insideCheck) {
            calculateConcaveHull(pointArrayList, k + 1)
        } else {
            return concaveHull
        }
    }
}

private val Point.x
    get() = coordinates.longitude

private val Point.y
    get() = coordinates.latitude
