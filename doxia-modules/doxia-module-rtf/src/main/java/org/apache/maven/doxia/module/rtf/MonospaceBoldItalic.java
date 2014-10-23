package org.apache.maven.doxia.module.rtf;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * @version $Id$
 */
class MonospaceBoldItalic
    extends FontMetrics
{
    static final CharMetrics[] METRICS = {new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 216, -15, 495, 572 ), new CharMetrics( 600, 0, 212, 277, 584, 562 ),
        new CharMetrics( 600, 0, 88, -45, 640, 651 ), new CharMetrics( 600, 0, 87, -126, 629, 666 ),
        new CharMetrics( 600, 0, 102, -15, 624, 616 ), new CharMetrics( 600, 0, 62, -15, 594, 543 ),
        new CharMetrics( 600, 0, 230, 277, 542, 562 ), new CharMetrics( 600, 0, 266, -102, 592, 616 ),
        new CharMetrics( 600, 0, 117, -102, 444, 616 ), new CharMetrics( 600, 0, 179, 219, 597, 601 ),
        new CharMetrics( 600, 0, 114, 39, 596, 478 ), new CharMetrics( 600, 0, 99, -111, 430, 174 ),
        new CharMetrics( 600, 0, 143, 203, 567, 313 ), new CharMetrics( 600, 0, 207, -15, 426, 171 ),
        new CharMetrics( 600, 0, 91, -77, 626, 626 ), new CharMetrics( 600, 0, 136, -15, 592, 616 ),
        new CharMetrics( 600, 0, 93, 0, 561, 616 ), new CharMetrics( 600, 0, 61, 0, 593, 616 ),
        new CharMetrics( 600, 0, 72, -15, 571, 616 ), new CharMetrics( 600, 0, 82, 0, 558, 616 ),
        new CharMetrics( 600, 0, 77, -15, 621, 601 ), new CharMetrics( 600, 0, 136, -15, 652, 616 ),
        new CharMetrics( 600, 0, 147, 0, 622, 601 ), new CharMetrics( 600, 0, 115, -15, 604, 616 ),
        new CharMetrics( 600, 0, 76, -15, 592, 616 ), new CharMetrics( 600, 0, 206, -15, 479, 425 ),
        new CharMetrics( 600, 0, 99, -111, 480, 425 ), new CharMetrics( 600, 0, 121, 15, 612, 501 ),
        new CharMetrics( 600, 0, 96, 118, 614, 398 ), new CharMetrics( 600, 0, 97, 15, 589, 501 ),
        new CharMetrics( 600, 0, 183, -14, 591, 580 ), new CharMetrics( 600, 0, 66, -15, 641, 616 ),
        new CharMetrics( 600, 0, -9, 0, 631, 562 ), new CharMetrics( 600, 0, 30, 0, 629, 562 ),
        new CharMetrics( 600, 0, 75, -18, 674, 580 ), new CharMetrics( 600, 0, 30, 0, 664, 562 ),
        new CharMetrics( 600, 0, 25, 0, 669, 562 ), new CharMetrics( 600, 0, 39, 0, 683, 562 ),
        new CharMetrics( 600, 0, 75, -18, 674, 580 ), new CharMetrics( 600, 0, 20, 0, 699, 562 ),
        new CharMetrics( 600, 0, 77, 0, 642, 562 ), new CharMetrics( 600, 0, 59, -18, 720, 562 ),
        new CharMetrics( 600, 0, 21, 0, 691, 562 ), new CharMetrics( 600, 0, 39, 0, 635, 562 ),
        new CharMetrics( 600, 0, -2, 0, 721, 562 ), new CharMetrics( 600, 0, 8, -12, 729, 562 ),
        new CharMetrics( 600, 0, 74, -18, 645, 580 ), new CharMetrics( 600, 0, 48, 0, 642, 562 ),
        new CharMetrics( 600, 0, 84, -138, 636, 580 ), new CharMetrics( 600, 0, 24, 0, 617, 562 ),
        new CharMetrics( 600, 0, 54, -22, 672, 582 ), new CharMetrics( 600, 0, 86, 0, 678, 562 ),
        new CharMetrics( 600, 0, 101, -18, 715, 562 ), new CharMetrics( 600, 0, 84, 0, 732, 562 ),
        new CharMetrics( 600, 0, 84, 0, 737, 562 ), new CharMetrics( 600, 0, 12, 0, 689, 562 ),
        new CharMetrics( 600, 0, 109, 0, 708, 562 ), new CharMetrics( 600, 0, 62, 0, 636, 562 ),
        new CharMetrics( 600, 0, 223, -102, 606, 616 ), new CharMetrics( 600, 0, 223, -77, 496, 626 ),
        new CharMetrics( 600, 0, 103, -102, 486, 616 ), new CharMetrics( 600, 0, 171, 250, 555, 616 ),
        new CharMetrics( 600, 0, -27, -125, 584, -75 ), new CharMetrics( 600, 0, 297, 277, 487, 562 ),
        new CharMetrics( 600, 0, 62, -15, 592, 454 ), new CharMetrics( 600, 0, 13, -15, 636, 626 ),
        new CharMetrics( 600, 0, 81, -15, 631, 459 ), new CharMetrics( 600, 0, 61, -15, 644, 626 ),
        new CharMetrics( 600, 0, 81, -15, 604, 454 ), new CharMetrics( 600, 0, 83, 0, 677, 626 ),
        new CharMetrics( 600, 0, 41, -146, 673, 454 ), new CharMetrics( 600, 0, 18, 0, 614, 626 ),
        new CharMetrics( 600, 0, 77, 0, 545, 658 ), new CharMetrics( 600, 0, 37, -146, 580, 658 ),
        new CharMetrics( 600, 0, 33, 0, 642, 626 ), new CharMetrics( 600, 0, 77, 0, 545, 626 ),
        new CharMetrics( 600, 0, -22, 0, 648, 454 ), new CharMetrics( 600, 0, 18, 0, 614, 454 ),
        new CharMetrics( 600, 0, 71, -15, 622, 454 ), new CharMetrics( 600, 0, -31, -142, 622, 454 ),
        new CharMetrics( 600, 0, 61, -142, 684, 454 ), new CharMetrics( 600, 0, 47, 0, 654, 454 ),
        new CharMetrics( 600, 0, 67, -17, 607, 459 ), new CharMetrics( 600, 0, 118, -15, 566, 562 ),
        new CharMetrics( 600, 0, 70, -15, 591, 439 ), new CharMetrics( 600, 0, 70, 0, 694, 439 ),
        new CharMetrics( 600, 0, 53, 0, 711, 439 ), new CharMetrics( 600, 0, 6, 0, 670, 439 ),
        new CharMetrics( 600, 0, -20, -142, 694, 439 ), new CharMetrics( 600, 0, 81, 0, 613, 439 ),
        new CharMetrics( 600, 0, 204, -102, 595, 616 ), new CharMetrics( 600, 0, 202, -250, 504, 750 ),
        new CharMetrics( 600, 0, 114, -102, 506, 616 ), new CharMetrics( 600, 0, 120, 153, 589, 356 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 77, 0, 545, 439 ),
        new CharMetrics( 600, 0, 272, 508, 503, 661 ), new CharMetrics( 600, 0, 313, 508, 608, 661 ),
        new CharMetrics( 600, 0, 212, 483, 606, 657 ), new CharMetrics( 600, 0, 200, 493, 642, 636 ),
        new CharMetrics( 600, 0, 195, 505, 636, 585 ), new CharMetrics( 600, 0, 217, 468, 651, 631 ),
        new CharMetrics( 600, 0, 346, 485, 490, 625 ), new CharMetrics( 600, 0, 244, 485, 592, 625 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 319, 481, 528, 678 ),
        new CharMetrics( 600, 0, 169, -206, 367, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 172, 488, 728, 661 ), new CharMetrics( 600, 0, 144, -199, 350, 0 ),
        new CharMetrics( 600, 0, 238, 493, 632, 667 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 197, -146, 477, 449 ), new CharMetrics( 600, 0, 121, -49, 604, 614 ),
        new CharMetrics( 600, 0, 107, -28, 650, 611 ), new CharMetrics( 600, 0, 77, 49, 643, 517 ),
        new CharMetrics( 600, 0, 98, 0, 709, 562 ), new CharMetrics( 600, 0, 218, -175, 488, 675 ),
        new CharMetrics( 600, 0, 74, -70, 619, 580 ), new CharMetrics( 600, 0, 244, 485, 592, 625 ),
        new CharMetrics( 600, 0, 53, -18, 667, 580 ), new CharMetrics( 600, 0, 189, 196, 526, 580 ),
        new CharMetrics( 600, 0, 63, 70, 638, 446 ), new CharMetrics( 600, 0, 135, 103, 617, 413 ),
        new CharMetrics( 600, 0, 143, 203, 567, 313 ), new CharMetrics( 600, 0, 53, -18, 667, 580 ),
        new CharMetrics( 600, 0, 195, 505, 636, 585 ), new CharMetrics( 600, 0, 173, 243, 569, 616 ),
        new CharMetrics( 600, 0, 76, 24, 614, 515 ), new CharMetrics( 600, 0, 192, 230, 541, 616 ),
        new CharMetrics( 600, 0, 193, 222, 525, 616 ), new CharMetrics( 600, 0, 313, 508, 608, 661 ),
        new CharMetrics( 600, 0, 50, -142, 591, 439 ), new CharMetrics( 600, 0, 61, -70, 699, 580 ),
        new CharMetrics( 600, 0, 249, 165, 461, 351 ), new CharMetrics( 600, 0, 169, -206, 367, 0 ),
        new CharMetrics( 600, 0, 213, 230, 514, 616 ), new CharMetrics( 600, 0, 189, 196, 542, 580 ),
        new CharMetrics( 600, 0, 72, 70, 647, 446 ), new CharMetrics( 600, 0, 14, -60, 706, 661 ),
        new CharMetrics( 600, 0, 23, -60, 715, 661 ), new CharMetrics( 600, 0, 8, -60, 698, 661 ),
        new CharMetrics( 600, 0, 101, -146, 509, 449 ), new CharMetrics( 600, 0, -9, 0, 631, 784 ),
        new CharMetrics( 600, 0, -9, 0, 665, 784 ), new CharMetrics( 600, 0, -9, 0, 631, 780 ),
        new CharMetrics( 600, 0, -9, 0, 638, 759 ), new CharMetrics( 600, 0, -9, 0, 631, 748 ),
        new CharMetrics( 600, 0, -9, 0, 631, 801 ), new CharMetrics( 600, 0, -29, 0, 707, 562 ),
        new CharMetrics( 600, 0, 74, -206, 674, 580 ), new CharMetrics( 600, 0, 25, 0, 669, 784 ),
        new CharMetrics( 600, 0, 25, 0, 669, 784 ), new CharMetrics( 600, 0, 25, 0, 669, 780 ),
        new CharMetrics( 600, 0, 25, 0, 669, 748 ), new CharMetrics( 600, 0, 77, 0, 642, 784 ),
        new CharMetrics( 600, 0, 77, 0, 642, 784 ), new CharMetrics( 600, 0, 77, 0, 642, 780 ),
        new CharMetrics( 600, 0, 77, 0, 642, 748 ), new CharMetrics( 600, 0, 30, 0, 664, 562 ),
        new CharMetrics( 600, 0, 8, -12, 729, 759 ), new CharMetrics( 600, 0, 74, -18, 645, 784 ),
        new CharMetrics( 600, 0, 74, -18, 645, 784 ), new CharMetrics( 600, 0, 74, -18, 645, 780 ),
        new CharMetrics( 600, 0, 74, -18, 668, 759 ), new CharMetrics( 600, 0, 74, -18, 645, 748 ),
        new CharMetrics( 600, 0, 105, 39, 606, 478 ), new CharMetrics( 600, 0, 48, -22, 672, 584 ),
        new CharMetrics( 600, 0, 101, -18, 715, 784 ), new CharMetrics( 600, 0, 101, -18, 715, 784 ),
        new CharMetrics( 600, 0, 101, -18, 715, 780 ), new CharMetrics( 600, 0, 101, -18, 715, 748 ),
        new CharMetrics( 600, 0, 109, 0, 708, 784 ), new CharMetrics( 600, 0, 48, 0, 619, 562 ),
        new CharMetrics( 600, 0, 22, -15, 628, 626 ), new CharMetrics( 600, 0, 62, -15, 592, 661 ),
        new CharMetrics( 600, 0, 62, -15, 608, 661 ), new CharMetrics( 600, 0, 62, -15, 592, 657 ),
        new CharMetrics( 600, 0, 62, -15, 642, 636 ), new CharMetrics( 600, 0, 62, -15, 592, 625 ),
        new CharMetrics( 600, 0, 62, -15, 592, 678 ), new CharMetrics( 600, 0, 21, -15, 651, 454 ),
        new CharMetrics( 600, 0, 81, -206, 631, 459 ), new CharMetrics( 600, 0, 81, -15, 604, 661 ),
        new CharMetrics( 600, 0, 81, -15, 608, 661 ), new CharMetrics( 600, 0, 81, -15, 606, 657 ),
        new CharMetrics( 600, 0, 81, -15, 604, 625 ), new CharMetrics( 600, 0, 77, 0, 545, 661 ),
        new CharMetrics( 600, 0, 77, 0, 608, 661 ), new CharMetrics( 600, 0, 77, 0, 566, 657 ),
        new CharMetrics( 600, 0, 77, 0, 552, 625 ), new CharMetrics( 600, 0, 93, -27, 661, 626 ),
        new CharMetrics( 600, 0, 18, 0, 642, 636 ), new CharMetrics( 600, 0, 71, -15, 622, 661 ),
        new CharMetrics( 600, 0, 71, -15, 622, 661 ), new CharMetrics( 600, 0, 71, -15, 622, 657 ),
        new CharMetrics( 600, 0, 71, -15, 642, 636 ), new CharMetrics( 600, 0, 71, -15, 622, 625 ),
        new CharMetrics( 600, 0, 114, 16, 596, 500 ), new CharMetrics( 600, 0, 55, -24, 637, 463 ),
        new CharMetrics( 600, 0, 70, -15, 591, 661 ), new CharMetrics( 600, 0, 70, -15, 608, 661 ),
        new CharMetrics( 600, 0, 70, -15, 591, 657 ), new CharMetrics( 600, 0, 70, -15, 591, 625 ),
        new CharMetrics( 600, 0, -20, -142, 694, 661 ), new CharMetrics( 600, 0, -31, -142, 622, 626 ),
        new CharMetrics( 600, 0, -20, -142, 694, 625 )};

    MonospaceBoldItalic()
    {
        super( true, 626, -142, new CharMetrics( 0, 0, -56, -250, 868, 801 ), METRICS );
    }
}
