package com.example.model

import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Represents a 2x2 Matrix for battlefield transformations:
 * | a  b |
 * | c  d |
 */
data class Matrix2x2(
    val a: Float, val b: Float,
    val c: Float, val d: Float
) {
    /**
     * Determinant: det(A) = ad - bc
     * Measures signed area scaling. If det == 0, the transformation is singular,
     * collapsing 2D space into a lower dimension (Singularity Strike!).
     */
    val det: Float
        get() = a * d - b * c

    val isSingular: Boolean
        get() = kotlin.math.abs(det) < 0.0001f

    val trace: Float
        get() = a + d

    /**
     * Matrix multiplication: C = A * B
     */
    operator fun times(other: Matrix2x2): Matrix2x2 {
        return Matrix2x2(
            a = this.a * other.a + this.b * other.c,
            b = this.a * other.b + this.b * other.d,
            c = this.c * other.a + this.d * other.c,
            d = this.c * other.b + this.d * other.d
        )
    }

    /**
     * Transform a position vector: p' = A * p
     */
    fun transform(p: GridPos): GridPos {
        val newX = (a * p.x + b * p.y).roundToInt()
        val newY = (c * p.x + d * p.y).roundToInt()
        return GridPos(newX, newY)
    }

    /**
     * Transform relative to a designated pivot center:
     * p' = A * (p - pivot) + pivot
     */
    fun transformAroundPivot(p: GridPos, pivot: GridPos): GridPos {
        val rel = p - pivot
        val transformedRel = transform(rel)
        return transformedRel + pivot
    }

    /**
     * Matrix inverse A^-1 = (1 / det) * |  d  -b |
     *                                   | -c   a |
     */
    fun inverse(): Matrix2x2? {
        val dVal = det
        if (kotlin.math.abs(dVal) < 0.0001f) return null
        val invDet = 1.0f / dVal
        return Matrix2x2(
            a = d * invDet,
            b = -b * invDet,
            c = -c * invDet,
            d = a * invDet
        )
    }

    /**
     * Calculate real eigenvalues:
     * lambda^2 - tr(A)*lambda + det(A) = 0
     * lambda = (tr +- sqrt(tr^2 - 4*det)) / 2
     */
    fun calculateEigenvalues(): Pair<Float, Float>? {
        val tr = trace
        val discriminant = tr * tr - 4f * det
        if (discriminant < 0f) return null // Complex eigenvalues (rotational field)
        val sqrtD = sqrt(discriminant)
        val l1 = (tr + sqrtD) / 2f
        val l2 = (tr - sqrtD) / 2f
        return Pair(l1, l2)
    }

    /**
     * Computes the dominant eigenvector (direction that scales by eigenvalue)
     * Useful for Eigenweapon alignment!
     */
    fun calculateDominantEigenvector(): Pair<Float, Float> {
        val eigenvalues = calculateEigenvalues()
        val lambda = eigenvalues?.first ?: 1.0f
        // (A - lambda*I)v = 0 -> (a - lambda)*x + b*y = 0
        val vx: Float
        val vy: Float
        if (kotlin.math.abs(b) > 0.0001f) {
            vx = -b
            vy = a - lambda
        } else if (kotlin.math.abs(c) > 0.0001f) {
            vx = d - lambda
            vy = -c
        } else {
            vx = 1f
            vy = 0f
        }
        val len = sqrt(vx * vx + vy * vy)
        return if (len > 0.0001f) Pair(vx / len, vy / len) else Pair(1f, 0f)
    }

    companion object {
        // Identity Matrix: I
        val IDENTITY = Matrix2x2(1f, 0f, 0f, 1f)

        // Rotation Matrices (clockwise in screen coords where Y goes down)
        // 90 deg: [0, -1; 1, 0]
        val ROTATION_90 = Matrix2x2(0f, -1f, 1f, 0f)
        val ROTATION_180 = Matrix2x2(-1f, 0f, 0f, -1f)
        val ROTATION_270 = Matrix2x2(0f, 1f, -1f, 0f)

        // Reflection Matrices
        val REFLECTION_HORIZONTAL = Matrix2x2(1f, 0f, 0f, -1f) // Flip Y
        val REFLECTION_VERTICAL = Matrix2x2(-1f, 0f, 0f, 1f)   // Flip X
        val REFLECTION_DIAGONAL = Matrix2x2(0f, 1f, 1f, 0f)    // Swap X, Y

        // Scaling Matrices
        val SCALE_2X = Matrix2x2(2f, 0f, 0f, 2f)
        val SCALE_HALF = Matrix2x2(0.5f, 0f, 0f, 0.5f)

        // Singularity Matrices (det = 0)
        val SINGULARITY_PROJECTOR = Matrix2x2(1f, 2f, 2f, 4f)
        val SHEAR_X = Matrix2x2(1f, 1f, 0f, 1f)
    }
}
