package com.example

import com.example.engine.MatrixMathEngine
import com.example.model.GridPos
import com.example.model.Matrix2x2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `test matrix rotation 90 degrees`() {
        // R(90) = [0, -1; 1, 0]
        val m = Matrix2x2.ROTATION_90
        val p = GridPos(2, 3)
        val transformed = m.transform(p)
        // x' = 0*2 - 1*3 = -3
        // y' = 1*2 + 0*3 = 2
        assertEquals(-3, transformed.x)
        assertEquals(2, transformed.y)
    }

    @Test
    fun `test singularity determinant equals zero`() {
        val singularMatrix = Matrix2x2(1f, 2f, 2f, 4f)
        assertEquals(0f, singularMatrix.det, 0.0001f)
        assertTrue(singularMatrix.isSingular)
    }

    @Test
    fun `test matrix multiplication noncommutativity`() {
        val r = Matrix2x2.ROTATION_90
        val f = Matrix2x2.REFLECTION_HORIZONTAL
        val rf = r * f
        val fr = f * r
        // F * R != R * F
        assertTrue(rf.a != fr.a || rf.b != fr.b || rf.c != fr.c || rf.d != fr.d)
    }

    @Test
    fun `test real eigenvalues computation`() {
        val m = Matrix2x2(3f, 0f, 0f, 0.5f)
        val eigenvalues = m.calculateEigenvalues()
        assertTrue(eigenvalues != null)
        assertEquals(3f, eigenvalues!!.first, 0.001f)
        assertEquals(0.5f, eigenvalues.second, 0.001f)
    }
}
